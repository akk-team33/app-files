package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Supplier;

public class FilePath extends FileProperty<FilePath> {

    private static final Comparator<FilePath> ORDER = neutralOrder();

    private final Path relative;

    public FilePath(final Supplier<Path> cwd, final FileEntry entry) {
        super(entry, FilePath.class, ORDER);
        this.relative = cwd.get().relativize(entry.path());
    }

    @Override
    public final String toString() {
        return relative.toString();
    }
}
