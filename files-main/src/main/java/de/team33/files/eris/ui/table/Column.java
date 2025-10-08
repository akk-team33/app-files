package de.team33.files.eris.ui.table;

import de.team33.sphinx.delta.table.RowColumnModel;

import javax.swing.*;
import java.util.function.Function;

@SuppressWarnings({"ClassNameSameAsAncestorName", "unused"})
public enum Column implements RowColumnModel.Column<Entry> {

    NAME(new Backing<>("Name", Property.Name.class, Entry::name, SwingConstants.LEADING)),
    LOCATION(new Backing<>("Location", Property.Location.class, Entry::location, SwingConstants.LEADING)),
    LAST_MODIFIED(new Backing<>("Last Modified", Property.LastModified.class, Entry::lastModified, SwingConstants.CENTER)),
    SIZE(new Backing<>("Size", Property.Size.class, Entry::size, SwingConstants.TRAILING));

    private final Backing<?> backing;

    <P> Column(final Backing<P> backing) {
        this.backing = backing;
    }

    @Override
    public String title() {
        return backing.title;
    }

    @Override
    public Class<?> type() {
        return backing.pClass;
    }

    @Override
    public Object map(final Entry element) {
        return backing.mapping().apply(element);
    }

    private record Backing<P>(String title,
                              Class<P> pClass,
                              Function<Entry, P> mapping,
                              int horizontalAlignment) {
    }
}
