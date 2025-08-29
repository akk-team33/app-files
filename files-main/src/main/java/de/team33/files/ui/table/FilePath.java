package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;

import java.util.Comparator;

public class FilePath extends FileProperty<FilePath> {

    private static Comparator<FilePath> ORDER =
            Comparator.comparing(FileProperty::entry, ENTRY_PATH);

    public FilePath(final FileEntry entry) {
        super(entry, FilePath.class, ORDER);
    }

    @Override
    public final String toString() {
        return entry().path().toString();
    }
}
