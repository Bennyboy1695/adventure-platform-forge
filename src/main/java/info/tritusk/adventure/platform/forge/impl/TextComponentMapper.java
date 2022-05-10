package info.tritusk.adventure.platform.forge.impl;

import info.tritusk.adventure.platform.forge.impl.visitor.ToNativeConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

public class TextComponentMapper {

    public static IFormattableTextComponent toNative(Component component) {
        final ToNativeConverter converter = new ToNativeConverter();
        converter.accept(component);
        return converter.getNative();
    }

    public static Component fromNative(ITextComponent component) {
        return GsonComponentSerializer.gson().deserialize(ITextComponent.Serializer.toJson(component));
    }
}
