package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;

class FileProperty {

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

    final FileEntry entry;

    FileProperty(final FileEntry entry) {
        this.entry = entry;
    }

    public final FileEntry entry() {
        return entry;
    }
}
