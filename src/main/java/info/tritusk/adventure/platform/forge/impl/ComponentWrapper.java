package info.tritusk.adventure.platform.forge.impl;

import info.tritusk.adventure.platform.forge.impl.visitor.ToNativeConverter;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import org.jetbrains.annotations.NotNull;

public class ComponentWrapper implements ITextComponent {

    private final Component wrapped;
    private final ITextComponent deepConverted;

    public ComponentWrapper(Component wrapped) {
        this.wrapped = wrapped;
        final ToNativeConverter converter = new ToNativeConverter();
        converter.accept(wrapped);
        this.deepConverted = converter.getNative();
    }

    public Component getWrapped() {
        return this.wrapped;
    }

    @Override
    public @NotNull Style getStyle() {
        return this.deepConverted.getStyle();
    }

    @Override
    public @NotNull List<ITextComponent> getSiblings() {
        return this.deepConverted.getSiblings();
    }

    @Override
    public @NotNull String getString() {
        return deepConverted.getString();
    }

    @Override
    public String getContents() {
        return this.deepConverted.getContents();
    }

    @Override
    public IFormattableTextComponent plainCopy() {
        return this.deepConverted.plainCopy();
    }

    @Override
    public IFormattableTextComponent copy() {
        return this.deepConverted.copy();
    }

    @Override
    public IReorderingProcessor getVisualOrderText() {
        return this.deepConverted.getVisualOrderText();
    }

    @Override
    public String getString(int p_212636_1_) {
        return this.deepConverted.getString(p_212636_1_);
    }

    @Override
    public <T> Optional<T> visit(IStyledTextAcceptor<T> p_230439_1_, Style p_230439_2_) {
        return this.deepConverted.visit(p_230439_1_, p_230439_2_);
    }

    @Override
    public <T> Optional<T> visit(ITextAcceptor<T> p_230438_1_) {
        return this.deepConverted.visit(p_230438_1_);
    }

    @Override
    public <T> Optional<T> visitSelf(IStyledTextAcceptor<T> p_230534_1_, Style p_230534_2_) {
        return this.deepConverted.visit(p_230534_1_, p_230534_2_);
    }

    @Override
    public <T> Optional<T> visitSelf(ITextAcceptor<T> p_230533_1_) {
        return this.deepConverted.visitSelf(p_230533_1_);
    }

    @Override
    public int hashCode() {
        return this.deepConverted.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.deepConverted.equals(obj);
    }

    @Override
    public String toString() {
        return this.deepConverted.toString();
    }

}
