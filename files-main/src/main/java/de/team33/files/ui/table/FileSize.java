package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;

import java.util.Comparator;

public class FileSize extends FileProperty<FileSize> {

    private static Comparator<FileSize> ORDER =
            Comparator.comparing(FileProperty::entry, ENTRY_SIZE);

    public FileSize(final FileEntry entry) {
        super(entry, FileSize.class, ORDER);
    }

    @Override
    public final String toString() {
        return "%,d".formatted(entry().size());
    }
}
