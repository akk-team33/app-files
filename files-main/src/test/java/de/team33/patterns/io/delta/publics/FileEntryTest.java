package de.team33.patterns.io.delta.publics;

import de.team33.patterns.io.delta.FileEntry;
import de.team33.patterns.io.delta.FileType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileEntryTest {

    private static final Path DEV_NULL = Paths.get("/", "dev", "null"); // special file
    private static final Path ROOT_DIR = Paths.get("/", "root"); // unreadable directory
    private static final Path ROOT = Paths.get("/"); // root directory

    static Stream<Path> paths() {
        return Stream.of(
                Paths.get("file", "is", "missing"),
                Paths.get("src", "main", "java"),
                Paths.get("pom.xml"),
                DEV_NULL,
                ROOT_DIR,
                ROOT);
    }

    private static String nameOf(final Path path) {
        return Optional.ofNullable(path.getFileName()).orElse(path).toString();
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void path(final Path path) {
        final FileEntry entry = FileEntry.of(path);
        assertTrue(entry.path().isAbsolute());
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void name(final Path path) {
        final FileEntry entry = FileEntry.of(path);
        assertEquals(nameOf(path), entry.name());
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void testToString(final Path path) {
        final FileEntry entry = FileEntry.of(path);
        assertEquals(path.toAbsolutePath().normalize().toString(), entry.toString());
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void types(final Path path) {
        final FileEntry entry = FileEntry.of(path);
        assertEquals(entry.isDirectory(), entry.types().contains(FileType.DIRECTORY));
        assertEquals(entry.isSymbolicLink(), entry.types().contains(FileType.SYMBOLIC_LINK));
        assertEquals(entry.isRegularFile(), entry.types().contains(FileType.REGULAR_FILE));
        assertEquals(entry.isSpecial(), entry.types().contains(FileType.SPECIAL));
        assertEquals(entry.isMissing(), entry.types().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void isDirectory(final Path path) {
        final FileEntry entry = FileEntry.of(path);
        assertEquals(Files.isDirectory(path), entry.isDirectory());
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void isRegularFile(final Path path) {
        final FileEntry entry = FileEntry.of(path);
        assertEquals(Files.isRegularFile(path), entry.isRegularFile());
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void isSymbolicLink(final Path path) {
        final FileEntry entry = FileEntry.of(path);
        assertEquals(Files.isSymbolicLink(path), entry.isSymbolicLink());
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void isOther(final Path path) {
        final FileEntry entry = FileEntry.of(path);
        if (entry.isSpecial()) {
            assertEquals(DEV_NULL, path);
        } else {
            assertNotEquals(DEV_NULL, path);
        }
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void isMissing(final Path path) {
        final FileEntry entry = FileEntry.of(path);
        assertEquals(!Files.exists(path), entry.isMissing());
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void lastModified(final Path path) throws IOException {
        final FileEntry entry = FileEntry.of(path);
        if (entry.isMissing()) {
            assertThrows(UnsupportedOperationException.class, entry::lastModified);
        } else {
            assertEquals(Files.getLastModifiedTime(path).toInstant(), entry.lastModified());
        }
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void lastUpdated(final Path path) {
        final FileEntry entry = FileEntry.of(path);
        if (entry.isBroken()) {
            assertThrows(UnsupportedOperationException.class, entry::lastUpdated);
        } else {
            assertNotNull(entry.lastUpdated());
        }
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void lastAccess(final Path path) {
        final FileEntry entry = FileEntry.of(path);
        if (entry.isMissing()) {
            assertThrows(UnsupportedOperationException.class, entry::lastAccess);
        } else {
            assertNotNull(entry.lastAccess());
        }
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void creation(final Path path) {
        final FileEntry entry = FileEntry.of(path);
        if (entry.isMissing()) {
            assertThrows(UnsupportedOperationException.class, entry::creation);
        } else {
            assertNotNull(entry.creation());
        }
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void size(final Path path) throws IOException {
        final FileEntry entry = FileEntry.of(path);
        if (entry.isMissing()) {
            assertThrows(UnsupportedOperationException.class, entry::size);
        } else {
            assertEquals(Files.size(path), entry.size());
        }
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void effectiveSize(final Path path) throws IOException {
        final FileEntry entry = FileEntry.of(path);
        if (entry.isMissing()) {
            assertThrows(UnsupportedOperationException.class, entry::effectiveSize);
        } else {
            //noinspection ObviousNullCheck
            assertNotNull(entry.effectiveSize());
        }
    }

    @ParameterizedTest
    @MethodSource("paths")
    final void content(final Path path) {
        final FileEntry entry = FileEntry.of(path);
        if (entry.isDirectory()) {
            assertNotNull(entry.entries());
        } else {
            assertThrows(UnsupportedOperationException.class, entry::entries);
        }
    }
}
