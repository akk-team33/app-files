package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.serving.alpha.Gettable;

import java.nio.file.Path;
import java.util.Comparator;

public class FileName extends FileProperty<FileName> {

    private static final Comparator<FileName> ORDER =
            Comparator.comparing(FileProperty::entry, ENTRY_NAME);

    public FileName(final Gettable<Path> ignored, final FileEntry entry) {
        super(entry, FileName.class, ORDER);
    }

    @Override
    public final String toString() {
        return entry().name();
    }
}
