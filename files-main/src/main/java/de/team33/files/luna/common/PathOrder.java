package de.team33.files.luna.common;

import java.nio.file.Path;
import java.util.Comparator;

import static java.util.Comparator.comparing;

@FunctionalInterface
public interface PathOrder extends Comparator<Path> {

    PathOrder NATURAL = comparing(Path::toString, StringOrder.NATURAL)::compare;
    PathOrder BY_NAME = comparing(Path::getFileName, NATURAL).thenComparing(NATURAL)::compare;
}
