package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

import static java.util.function.Predicate.not;

public class FileParent extends FileProperty<FileParent> {

    private static Comparator<FileParent> ORDER =
            Comparator.comparing(FileProperty::entry, ENTRY_PATH);
    private final Path parent;

    public FileParent(final FileEntry entry) {
        super(entry, FileParent.class, ORDER);
        this.parent = entry.path().getParent();
    }

    @Override
    public final String toString() {
        return Optional.ofNullable(parent)
                       .map(Path::toString)
                       .filter(not(String::isBlank))
                       .orElse(".");
    }
}
