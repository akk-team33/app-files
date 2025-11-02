package de.team33.files.phobos.model;

import de.team33.patterns.proving.kerberos.Guard;
import de.team33.patterns.streamable.galatea.Streamer;

import java.nio.file.Path;
import java.util.List;

public class History {

    private final List<Path> paths;
    private final int index;

    public History(final Path path) {
        this(List.of(path), 0);
    }

    private History(final List<Path> paths, final Path path) {
        this(Streamer.of(paths).add(path).stream().toList(), paths.size());
    }

    private History(final List<Path> paths, final int index) {
        final int size = paths.size();
        Guard.prove(0 < size,
                    () -> "<paths> is expected to be not empty - but was %s".formatted(paths));
        Guard.prove((0 <= index) && (index < size),
                    () -> "<index> is expected to be between 0 and %d - but was %d".formatted(size - 1, index));
        this.paths = paths;
        this.index = index;
    }

    public final History setCurrent(final Path path) {
        if (paths.get(index).equals(path)) {
            return this;
        } else {
            return new History(paths.subList(0, index + 1), path);
        }
    }

    public final History prev() {
        return hasPrev() ? new History(paths, index - 1) : this;
    }

    public final History next() {
        return hasNext() ? new History(paths, index + 1) : this;
    }

    public final boolean hasPrev() {
        return 0 < index;
    }

    public final boolean hasNext() {
        return (index + 1) < paths.size();
    }

    public final Path path() {
        return paths.get(index);
    }
}
