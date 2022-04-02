package info.tritusk.adventure.platform.forge.impl.audience;

import info.tritusk.adventure.platform.forge.impl.ComponentWrapper;
import info.tritusk.adventure.platform.forge.impl.TextComponentMapper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.permission.PermissionChecker;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraftforge.server.permission.PermissionAPI;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link Audience} that represents the server console.
 * Note that this type of audience only supports text message.
 */
public class ServerAudience implements Audience {
    private final MinecraftServer theServer;

    public ServerAudience(MinecraftServer theServer) {
        this.theServer = theServer;
    }

    @Override
    public void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
        // TODO Should we somehow display the source? By default, it only prints out the message.
        theServer.sendMessage(TextComponentMapper.toNative(message), source.uuid());
    }

    @Override
    public @NotNull Pointers pointers() {
        final Pointers.Builder builder = Pointers.builder();
        builder.withStatic(Identity.UUID, Util.NIL_UUID);
        builder.withStatic(Identity.NAME, "Console");
        builder.withDynamic(Identity.DISPLAY_NAME, () ->  Component.text("Console"));
        builder.withStatic(PermissionChecker.POINTER, permission -> TriState.TRUE);
        return builder.build();
    }
}
