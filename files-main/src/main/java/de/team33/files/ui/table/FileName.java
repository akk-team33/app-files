package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;

import java.util.Comparator;

public class FileName extends FileProperty implements Comparable<FileName> {

    private static Comparator<FileName> ORDER =
            Comparator.comparing(fileName -> fileName.entry, ENTRY_NAME);

    FileName(final FileEntry entry) {
        super(entry);
    }

    @Override
    public final int compareTo(final FileName other) {
        return FileProperty.ENTRY_NAME.compare(entry, other.entry);
    }
}
