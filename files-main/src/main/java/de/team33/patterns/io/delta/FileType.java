package de.team33.patterns.io.delta;

import de.team33.patterns.enums.pan.Values;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Symbolizes different file types
 */
public enum FileType {

    /**
     * Symbolizes a regular file.
     */
    REGULAR_FILE(BasicFileAttributes::isRegularFile),

    /**
     * Symbolizes a directory.
     */
    DIRECTORY(BasicFileAttributes::isDirectory),

    /**
     * Symbolizes a symbolic link.
     */
    SYMBOLIC_LINK(BasicFileAttributes::isSymbolicLink),

    /**
     * Symbolizes a special file.
     */
    SPECIAL(BasicFileAttributes::isOther);

    private static final Values<FileType> VALUES = Values.of(FileType.class);

    private final Predicate<BasicFileAttributes> filter;

    FileType(final Predicate<BasicFileAttributes> filter) {
        this.filter = filter;
    }

    static Stream<FileType> matching(final BasicFileAttributes attributes) {
        return VALUES.stream()
                     .filter(fileType -> fileType.filter.test(attributes));
    }
}
