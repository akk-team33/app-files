package de.team33.patterns.io.delta.publics;

import de.team33.patterns.io.delta.FileEntry;
import de.team33.patterns.io.delta.FileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SymbolicLinkTest {

    private static final Path BASE_PATH = Paths.get("target", "testing", SymbolicLinkTest.class.getSimpleName())
                                               .toAbsolutePath()
                                               .normalize();
    @SuppressWarnings("HardcodedFileSeparator")
    private static final Path SPECIAL_PATH = Paths.get("/dev/null");

    private Path regLinkPath;
    private Path dirLinkPath;
    private Path specLinkPath;
    private Path linkLinkPath;
    private Path missingLinkPath;

    @BeforeEach
    final void setUp() throws IOException {
        final Path testPath = BASE_PATH.resolve(UUID.randomUUID().toString());
        final Path dirPath = testPath.resolve("directory");
        final Path regularPath = testPath.resolve("regular.file");
        final Path missingPath = testPath.resolve("missing.file");

        regLinkPath = testPath.resolve("regular.link");
        dirLinkPath = testPath.resolve("directory.link");
        specLinkPath = testPath.resolve("special.link");
        linkLinkPath = testPath.resolve("indirect.link");
        missingLinkPath = testPath.resolve("missing.link");

        Files.createDirectories(dirPath);
        Files.writeString(regularPath, UUID.randomUUID().toString());
        Files.createSymbolicLink(regLinkPath, regularPath.getFileName());
        Files.createSymbolicLink(dirLinkPath, dirPath.getFileName());
        Files.createSymbolicLink(missingLinkPath, missingPath.getFileName());
        Files.createSymbolicLink(specLinkPath, SPECIAL_PATH);
        Files.createSymbolicLink(linkLinkPath, regLinkPath.getFileName());
    }

    @Test
    final void linkRegular() {
        final FileEntry result = FileEntry.of(regLinkPath);
        assertEquals(Set.of(FileType.SYMBOLIC_LINK, FileType.REGULAR_FILE), result.types());
        assertTrue(result.isSymbolicLink());
        assertTrue(result.isRegularFile());
    }

    @Test
    final void linkDirectory() {
        final FileEntry result = FileEntry.of(dirLinkPath);
        assertEquals(Set.of(FileType.SYMBOLIC_LINK, FileType.DIRECTORY), result.types());
        assertTrue(result.isSymbolicLink());
        assertTrue(result.isDirectory());
    }

    @Test
    final void linkMissing() {
        final FileEntry result = FileEntry.of(missingLinkPath);
        assertEquals(Set.of(FileType.SYMBOLIC_LINK), result.types());
        assertTrue(result.isSymbolicLink());
        assertTrue(result.isMissing());
    }

    @Test
    final void linkSpecial() {
        final FileEntry result = FileEntry.of(specLinkPath);
        assertEquals(Set.of(FileType.SYMBOLIC_LINK, FileType.SPECIAL), result.types());
        assertTrue(result.isSymbolicLink());
        assertTrue(result.isSpecial());
    }

    @Test
    final void linkLinkRegular() {
        final FileEntry result = FileEntry.of(linkLinkPath);
        assertEquals(Set.of(FileType.SYMBOLIC_LINK, FileType.REGULAR_FILE), result.types());
        assertTrue(result.isSymbolicLink());
        assertTrue(result.isRegularFile());
    }
}
