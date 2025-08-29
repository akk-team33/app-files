package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.serving.alpha.Gettable;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

import static java.util.function.Predicate.not;

public class FileParent extends FileProperty<FileParent> {

    private static Comparator<FileParent> ORDER =
            Comparator.comparing(FileProperty::entry, ENTRY_PATH);
    private final Optional<Path> parent;

    public FileParent(final Gettable<Path> cwd, final FileEntry entry) {
        super(entry, FileParent.class, ORDER);
        this.parent = Optional.ofNullable(entry.path().getParent())
                              .map(p -> cwd.get()
                                           .relativize(p));
    }

    @Override
    public final String toString() {
        return parent.map(Path::toString)
                     .filter(not(String::isBlank))
                     .orElse(".");
    }
}
