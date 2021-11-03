package info.tritusk.adventure.platform.forge;

import info.tritusk.adventure.platform.forge.impl.BossBarMapper;
import info.tritusk.adventure.platform.forge.impl.BossInfoListener;
import info.tritusk.adventure.platform.forge.impl.ForgePlatform;
import info.tritusk.adventure.platform.forge.impl.KeyMapper;
import info.tritusk.adventure.platform.forge.impl.audience.AllServerPlayerAudience;
import info.tritusk.adventure.platform.forge.impl.audience.FilteredServerPlayerAudience;
import info.tritusk.adventure.platform.forge.impl.audience.ServerAudience;
import info.tritusk.adventure.platform.forge.impl.audience.ServerPlayerAudience;
import java.util.IdentityHashMap;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.server.permission.PermissionAPI;
import org.jetbrains.annotations.NotNull;

public class ForgeServerAudiences implements AudienceProvider {

    private final MinecraftServer server;
    private final ServerAudience theServerAudience;
    private final IdentityHashMap<BossBar, ServerBossInfo> trackedBossBars = new IdentityHashMap<>();
    public ForgeServerAudiences(MinecraftServer server) {
        this.theServerAudience = new ServerAudience(this.server = server);
    }

    public static ForgeServerAudiences of() {
        return ForgePlatform.serverAudienceProvider; // TODO Throw exception when it is not ready
    }    private final BossInfoListener listener = new BossInfoListener(this::getOrCreateFrom);

    public ServerBossInfo getOrCreateFrom(BossBar bossBar) {
        return this.trackedBossBars.computeIfAbsent(bossBar, bar -> {
            bar.addListener(this.listener);
            return BossBarMapper.toNative(bar);
        });
    }

    @Override
    public @NotNull Audience all() {
        return Audience.audience(this.players(), this.console());
    }

    @Override
    public @NotNull Audience console() {
        return this.theServerAudience;
    }

    @Override
    public @NotNull Audience players() {
        return new AllServerPlayerAudience(this.server.getPlayerList(), this::getOrCreateFrom);
    }

    @Override
    public @NotNull Audience player(@NotNull UUID playerId) {
        final ServerPlayerEntity p = this.server.getPlayerList().getPlayer(playerId);
        return p == null ? Audience.empty() : new ServerPlayerAudience(p, this::getOrCreateFrom);
    }

    @Override
    public @NotNull Audience permission(@NotNull Key permission) {
        return AudienceProvider.super.permission(permission);
    }

    @Override
    public @NotNull Audience permission(@NotNull String permission) {
        return new FilteredServerPlayerAudience(this.server.getPlayerList(), this::getOrCreateFrom, p -> PermissionAPI.hasPermission(p, permission));
    }

    @Override
    public @NotNull Audience world(@NotNull Key world) {
        final RegistryKey<World> worldKey = RegistryKey.create(Registry.DIMENSION_REGISTRY, KeyMapper.toNative(world));
        return this.server.getLevel(worldKey) == null ? Audience.empty() :
            new FilteredServerPlayerAudience(this.server.getPlayerList(), this::getOrCreateFrom, p -> p.level.dimension() == worldKey);
    }

    @Override
    public @NotNull Audience server(@NotNull String serverName) {
        return this.all();
    }

    @Override
    public @NotNull ComponentFlattener flattener() {
        return null;
    }

    @Override
    public void close() {
        // TODO What kind of clean up do we need here?
    }


}
