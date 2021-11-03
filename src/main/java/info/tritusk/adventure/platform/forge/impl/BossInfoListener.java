package info.tritusk.adventure.platform.forge.impl;

import java.util.Set;
import java.util.function.Function;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.minecraft.world.BossInfo;
import org.jetbrains.annotations.NotNull;

public class BossInfoListener implements BossBar.Listener {

    private final Function<BossBar, ? extends BossInfo> mapper;

    public BossInfoListener(Function<BossBar, ? extends BossInfo> mapper) {
        this.mapper = mapper;
    }

    @Override
    public void bossBarNameChanged(@NotNull BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
        if (oldName != newName) {
            this.mapper.apply(bar).setName(TextComponentMapper.toNative(newName));
        }
    }

    @Override
    public void bossBarProgressChanged(@NotNull BossBar bar, float oldProgress, float newProgress) {
        if (oldProgress != newProgress) {
            this.mapper.apply(bar).setPercent(newProgress);
        }
    }

    @Override
    public void bossBarColorChanged(@NotNull BossBar bar, BossBar.@NotNull Color oldColor, BossBar.@NotNull Color newColor) {
        if (oldColor != newColor) {
            this.mapper.apply(bar).setColor(BossBarMapper.toNative(newColor));
        }
    }

    @Override
    public void bossBarOverlayChanged(@NotNull BossBar bar, BossBar.@NotNull Overlay oldOverlay, BossBar.@NotNull Overlay newOverlay) {
        if (oldOverlay != newOverlay) {
            this.mapper.apply(bar).setOverlay(BossBarMapper.toNative(newOverlay));
        }
    }

    @Override
    public void bossBarFlagsChanged(@NotNull BossBar bar, @NotNull Set<BossBar.Flag> flagsAdded, @NotNull Set<BossBar.Flag> flagsRemoved) {
        final BossInfo bossInfo = this.mapper.apply(bar);
        if (flagsAdded.contains(BossBar.Flag.CREATE_WORLD_FOG)) {
            bossInfo.setCreateWorldFog(true);
        }
        if (flagsAdded.contains(BossBar.Flag.DARKEN_SCREEN)) {
            bossInfo.setDarkenScreen(true);
        }
        if (flagsAdded.contains(BossBar.Flag.PLAY_BOSS_MUSIC)) {
            bossInfo.setPlayBossMusic(true);
        }
        if (flagsRemoved.contains(BossBar.Flag.CREATE_WORLD_FOG)) {
            bossInfo.setCreateWorldFog(false);
        }
        if (flagsRemoved.contains(BossBar.Flag.DARKEN_SCREEN)) {
            bossInfo.setDarkenScreen(false);
        }
        if (flagsRemoved.contains(BossBar.Flag.PLAY_BOSS_MUSIC)) {
            bossInfo.setPlayBossMusic(false);
        }
    }

}
