package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.sphinx.gamma.table.CellProperty;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;

abstract class FileProperty<P extends FileProperty<P>> extends CellProperty<P> {

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

    FileProperty(final FileEntry entry, final Class<P> pClass, final Comparator<P> order) {
        super(pClass, order);
        this.entry = entry;
    }

    public final FileEntry entry() {
        return entry;
    }

    @Override
    protected final Path hashCriterion() {
        return entry.path();
    }
}
