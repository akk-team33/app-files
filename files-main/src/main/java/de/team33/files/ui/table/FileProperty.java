package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;

abstract class FileProperty<P extends FileProperty<P>> implements Comparable<P> {

    private static final Comparator<String> STRING_IGNORE_CASE =
            String::compareToIgnoreCase;
    private static final Comparator<String> STRING_RESPECT_CASE =
            String::compareTo;
    private static final Comparator<String> STRING_NORMAL =
            STRING_IGNORE_CASE.thenComparing(STRING_RESPECT_CASE);
    private static final Comparator<Path> PATH_NORMAL =
            Comparator.comparing(Path::toString, STRING_NORMAL);
    static final Comparator<FileEntry> ENTRY_PATH =
            Comparator.comparing(FileEntry::path, PATH_NORMAL);
    static final Comparator<FileEntry> ENTRY_NAME =
            Comparator.comparing(FileEntry::name, STRING_NORMAL)
                      .thenComparing(ENTRY_PATH);
    static final Comparator<FileEntry> ENTRY_LAST_MODIFIED =
            Comparator.comparing(FileEntry::lastModified, Instant::compareTo)
                      .thenComparing(ENTRY_PATH);
    static final Comparator<FileEntry> ENTRY_SIZE =
            Comparator.comparing(FileEntry::size, Long::compareTo)
                      .thenComparing(ENTRY_PATH);

    private final FileEntry entry;
    private final Class<P> pClass;
    private final Comparator<P> order;

    FileProperty(final FileEntry entry, final Class<P> pClass, final Comparator<P> order) {
        this.entry = entry;
        this.pClass = pClass;
        this.order = order;
        // fast fail if so ...
        pClass.cast(this);
    }

    public final FileEntry entry() {
        return entry;
    }

    @Override
    public final int compareTo(final P other) {
        return order.compare(pClass.cast(this), other);
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || (pClass.isInstance(obj) && (0 == compareTo(pClass.cast(obj))));
    }

    @Override
    public final int hashCode() {
        return entry.path().hashCode();
    }

    @Override
    public abstract String toString();
}
