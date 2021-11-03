package info.tritusk.adventure.platform.forge.impl.audience;

import info.tritusk.adventure.platform.forge.impl.ComponentWrapper;
import info.tritusk.adventure.platform.forge.impl.ForgePlatform;
import info.tritusk.adventure.platform.forge.impl.KeyMapper;
import info.tritusk.adventure.platform.forge.impl.SoundMapper;
import info.tritusk.adventure.platform.forge.impl.TextComponentMapper;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.function.Function;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SPlayerListHeaderFooterPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SStopSoundPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerBossInfo;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link Audience} that represents a particular online player.
 */
public class ServerPlayerAudience implements Audience {

    private final WeakReference<ServerPlayerEntity> player;
    private final Function<BossBar, ServerBossInfo> bossBarMapper;

    public ServerPlayerAudience(ServerPlayerEntity p, Function<BossBar, ServerBossInfo> bossBarMapper) {
        this.player = new WeakReference<>(p);
        this.bossBarMapper = bossBarMapper;
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        final ComponentWrapper realMsg = new ComponentWrapper(message);
        final ChatType chatType = type == MessageType.SYSTEM ? ChatType.SYSTEM : ChatType.CHAT;
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.sendMessage(realMsg.copy(), chatType, source.uuid());
        }
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        final ITextComponent realMsg = new ComponentWrapper(message);
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.displayClientMessage(realMsg.copy(), true);

        }
    }

    @Override
    public void sendPlayerListHeader(@NotNull Component header) {
        // TODO We might want to preserve the old footer
        this.sendPlayerListHeaderAndFooter(header, Component.empty());
    }

    @Override
    public void sendPlayerListFooter(@NotNull Component footer) {
        // TODO We might want to preserve the old header
        this.sendPlayerListHeaderAndFooter(Component.empty(), footer);
    }

    @Override
    public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
        final ITextComponent realHeader = new ComponentWrapper(header);
        final ITextComponent realFooter = new ComponentWrapper(footer);
        final SPlayerListHeaderFooterPacket packet = new SPlayerListHeaderFooterPacket();
        final PacketBuffer bridge = new PacketBuffer(Unpooled.buffer())
            .writeComponent(realHeader.copy())
            .writeComponent(realFooter.copy());
        try {
            // TODO Stop it, this is !@*#!@%#(!&@#)(! go use access transformer
            bridge.resetReaderIndex();
            packet.read(bridge);
        } catch (IOException e) {
            throw new RuntimeException("YELL AT THE AUTHOR TO ACTUALLY USE ACCESS TRANSFORMER", e);
        } finally {
            bridge.release();
        }
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.connection.send(packet);
        }
    }

    @Override
    public void showTitle(@NotNull Title title) {
        Title.Times titleTimes = title.times();
        if (titleTimes == null) {
            // How are you suppose to handle this case anyway?
            this.clearTitle();
        } else {
            final ServerPlayerEntity p = this.player.get();
            if (p != null) {
                p.connection.send(new STitlePacket(STitlePacket.Type.TITLE, TextComponentMapper.toNative(title.title())));
                p.connection.send(new STitlePacket(STitlePacket.Type.SUBTITLE, TextComponentMapper.toNative(title.subtitle())));
                p.connection.send(new STitlePacket((int) titleTimes.fadeIn().toMillis() / 50, (int) titleTimes.stay().toMillis() / 50,
                    (int) titleTimes.fadeOut().toMillis() / 50));
            }
        }
    }

    @Override
    public void clearTitle() {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.connection.send(new STitlePacket(STitlePacket.Type.CLEAR, StringTextComponent.EMPTY));
        }
    }

    @Override
    public void resetTitle() {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.connection.send(new STitlePacket(STitlePacket.Type.RESET, StringTextComponent.EMPTY));
        }
    }

    @Override
    public void showBossBar(@NotNull BossBar bar) {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            final ServerBossInfo nativeBossInfo = this.bossBarMapper.apply(bar);
            nativeBossInfo.addPlayer(p);
        }
    }

    @Override
    public void hideBossBar(@NotNull BossBar bar) {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            final ServerBossInfo nativeBossInfo = this.bossBarMapper.apply(bar);
            nativeBossInfo.removePlayer(p);
        }
    }

    @Override
    public void playSound(@NotNull Sound sound) {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.connection.send(new SPlaySoundPacket(KeyMapper.toNative(sound.name()), SoundMapper.toNative(sound.source()),
                p.position(), sound.volume(), sound.pitch()));
        }
    }

    @Override
    public void playSound(@NotNull Sound sound, double x, double y, double z) {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.connection.send(new SPlaySoundPacket(KeyMapper.toNative(sound.name()), SoundMapper.toNative(sound.source()),
                new Vector3d(x, y, z), sound.volume(), sound.pitch()));
        }
    }

    @Override
    public void stopSound(@NotNull SoundStop stop) {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.connection.send(new SStopSoundPacket(KeyMapper.toNative(stop.sound()), SoundMapper.toNative(stop.source())));
        }
    }

    @Override
    public void openBook(@NotNull Book book) {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            final ItemStack fakeBookItem = new ItemStack(Items.WRITTEN_BOOK);
            final CompoundNBT data = fakeBookItem.getOrCreateTag();
            data.putString("title", PlainTextComponentSerializer.plainText().serialize(book.title()));
            data.putString("author", PlainTextComponentSerializer.plainText().serialize(book.author()));
            final ListNBT pages = new ListNBT();
            for (Component page : book.pages()) {
                pages.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(TextComponentMapper.toNative(page))));
            }
            data.put("pages", pages);
            data.putBoolean("resolved", true);

            // Hack: swap out the item on main hand to trick Minecraft to open the book, then swap back
            final ItemStack previous = p.getMainHandItem();
            p.connection.send(new SSetSlotPacket(-2, p.inventory.selected, fakeBookItem));
            p.openItemGui(fakeBookItem, Hand.MAIN_HAND);
            p.connection.send(new SSetSlotPacket(-2, p.inventory.selected, previous));
        }
    }
}
