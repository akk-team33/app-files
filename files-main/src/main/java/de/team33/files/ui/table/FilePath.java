package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.serving.alpha.Gettable;

import java.nio.file.Path;
import java.util.Comparator;

public class FilePath extends FileProperty<FilePath> {

    private static Comparator<FilePath> ORDER =
            Comparator.comparing(FileProperty::entry, ENTRY_PATH);

    private final Path relative;

    public FilePath(final Gettable<Path> cwd, final FileEntry entry) {
        super(entry, FilePath.class, ORDER);
        this.relative = cwd.get().relativize(entry.path());
    }

    @Override
    public final String toString() {
        return relative.toString();
    }
}
