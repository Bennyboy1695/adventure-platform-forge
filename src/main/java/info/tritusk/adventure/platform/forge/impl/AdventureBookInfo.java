package info.tritusk.adventure.platform.forge.impl;

import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.inventory.Book;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import org.jetbrains.annotations.NotNull;

public class AdventureBookInfo implements ReadBookScreen.IBookInfo {

    private final Book book;
    private final List<ITextComponent> pages;

    public AdventureBookInfo(Book book) {
        this.book = book;
        this.pages = book.pages().stream().map(ComponentWrapper::new).collect(Collectors.toList());
    }

    @Override
    public int getPageCount() {
        return this.book.pages().size();
    }

    @Override
    public @NotNull ITextProperties getPage(int index) {
        return this.pages.get(index);
    }

    //TODO
    @Override
    public ITextProperties getPageRaw(int p_230456_1_) {
        return null;
    }

}
