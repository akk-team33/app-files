package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.sphinx.gamma.table.ColumnProperty;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;

abstract class FileProperty<P extends FileProperty<P>> extends ColumnProperty<P> {

    private static final Comparator<String> STRING_IGNORE_CASE =
            String::compareToIgnoreCase;
    private static final Comparator<String> STRING_RESPECT_CASE =
            String::compareTo;
    private static final Comparator<String> STRING_NORMAL =
            STRING_IGNORE_CASE.thenComparing(STRING_RESPECT_CASE);
    private static final Comparator<Path> PATH_NORMAL =
            Comparator.comparing(Path::toString, STRING_NORMAL);
    private static final Comparator<FileEntry> ENTRY_PATH =
            Comparator.comparing(FileEntry::path, PATH_NORMAL);
    static final Comparator<FileEntry> ENTRY_NAME =
            Comparator.comparing(FileEntry::name, STRING_NORMAL);
    static final Comparator<FileEntry> ENTRY_LAST_MODIFIED =
            Comparator.comparing(FileEntry::lastModified, Instant::compareTo);
    static final Comparator<FileEntry> ENTRY_SIZE =
            Comparator.comparing(FileEntry::size, Long::compareTo);

    private final FileEntry entry;

    FileProperty(final FileEntry entry, final Class<P> pClass, final Comparator<P> primaryOrder) {
        super(pClass, primaryOrder.thenComparing(FileProperty::entry, ENTRY_PATH));
        this.entry = entry;
    }

    static <P> Comparator<P> neutralOrder() {
        return (left, right) -> 0;
    }

    public final FileEntry entry() {
        return entry;
    }

    @Override
    public final boolean equals(final Object other) {
        // consistently with <compareTo()> ...
        return ColumnProperty.equals(this, other);
    }

    @Override
    public final int hashCode() {
        // consistently with <equals()> and <compareTo()>:
        // final order depends on file entry path (see constructor) ...
        return entry.path().hashCode();
    }
}
