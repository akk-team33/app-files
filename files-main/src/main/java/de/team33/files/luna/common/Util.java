package de.team33.files.luna.common;

import de.team33.patterns.decision.thyone.Choices;
import de.team33.patterns.io.adrastea.FileEntry;

import java.util.function.Function;

final class Util {

    private static final Function<FileEntries, Integer> COMPARE_TYPE =
            Choices.parallel(FileEntries::isLeftDirectory, FileEntries::isRightDirectory)
                   .replying(0, 1, -1, 0);

    private Util() {
    }

    static int compareByType(final FileEntry left, final FileEntry right) {
        return COMPARE_TYPE.apply(new FileEntries(left, right));
    }

    private record FileEntries(FileEntry left, FileEntry right) {

        final boolean isLeftDirectory() {
            return left.isDirectory();
        }

        final boolean isRightDirectory() {
            return right.isDirectory();
        }
    }
}
