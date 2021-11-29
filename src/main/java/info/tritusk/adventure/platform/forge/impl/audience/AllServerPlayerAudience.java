package info.tritusk.adventure.platform.forge.impl.audience;

import java.lang.ref.WeakReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.server.ServerBossInfo;
import org.jetbrains.annotations.NotNull;

public class AllServerPlayerAudience implements ForwardingAudience {

    private final WeakReference<PlayerList> players;
    private final Function<BossBar, ServerBossInfo> bossBarMapper;

    public AllServerPlayerAudience(PlayerList players, Function<BossBar, ServerBossInfo> bossBarMapper) {
        this.players = new WeakReference<>(players);
        this.bossBarMapper = bossBarMapper;
    }

    protected @NotNull Stream<ServerPlayerEntity> players() {
        final PlayerList list = this.players.get();
        return list != null ? list.getPlayers().stream() : Stream.empty();
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return this.players().map(p -> new ServerPlayerAudience(p, this.bossBarMapper)).collect(Collectors.toList());
    }
}
