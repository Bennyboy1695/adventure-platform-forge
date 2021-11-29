package info.tritusk.adventure.platform.forge.impl;

import net.kyori.adventure.util.Codec;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import org.jetbrains.annotations.NotNull;

public class JsonLikeNBTCodec implements Codec<CompoundNBT, String, Exception, Exception> {

    public static final JsonLikeNBTCodec INSTANCE = new JsonLikeNBTCodec();

    @Override
    public @NotNull CompoundNBT decode(@NotNull String encoded) throws Exception {
        return JsonToNBT.parseTag(encoded);
    }

    @Override
    public @NotNull String encode(@NotNull CompoundNBT decoded) {
        return decoded.toString();
    }
}
