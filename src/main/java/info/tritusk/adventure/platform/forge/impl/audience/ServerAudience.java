package info.tritusk.adventure.platform.forge.impl.audience;

import info.tritusk.adventure.platform.forge.impl.TextComponentMapper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
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
}
