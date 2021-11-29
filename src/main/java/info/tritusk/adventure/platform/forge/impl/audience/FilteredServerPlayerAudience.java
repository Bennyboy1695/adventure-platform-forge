package info.tritusk.adventure.platform.forge.impl.audience;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.server.ServerBossInfo;
import org.jetbrains.annotations.NotNull;

public class FilteredServerPlayerAudience extends AllServerPlayerAudience {

    private final Predicate<ServerPlayerEntity> filter;

    public FilteredServerPlayerAudience(PlayerList players, Function<BossBar, ServerBossInfo> bossBarMapper, Predicate<ServerPlayerEntity> filter) {
        super(players, bossBarMapper);
        this.filter = filter;
    }

    @Override
    protected @NotNull Stream<ServerPlayerEntity> players() {
        return super.players().filter(this.filter);
    }
}
