package de.team33.patterns.io.delta;

import de.team33.patterns.lazy.narvi.Lazy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Comparator.comparing;

/**
 * Represents an entry from a virtual file index.
 * Includes some meta information about a file, particularly the file system path, file type, size,
 * and some timestamps.
 * <p>
 * Strictly speaking, the meta information only applies to the moment of instantiation.
 * Therefore, an instance should be short-lived. The longer an instance "lives", the more likely it is
 * that the meta information is out of date because the underlying file may have been changed in the meantime.
 */
public class FileEntry {

    private static final String PROPERTY_NOT_AVAILABLE =
            "property not available because the file does not exist:%n%n" +
            "    path: %s%n%n";
    private static final String ENTRIES_NOT_AVAILABLE =
            "entries not available because the file is not a directory:%n%n" +
            "    path: %s%n%n";

    private static final Comparator<String> PRIMARY = String::compareToIgnoreCase;
    private static final Comparator<String> SECONDARY = String::compareTo;
    private static final Comparator<FileEntry> ENTRY_ORDER = comparing(FileEntry::name,
                                                                       PRIMARY.thenComparing(SECONDARY));
    private static final LinkOption[] DISTINCTIVE = {LinkOption.NOFOLLOW_LINKS};
    private static final LinkOption[] RESOLVING = {};
    @SuppressWarnings("StaticCollection") // Set is immutable
    private static final Set<FileType> BROKEN_LINK = Set.of(FileType.SYMBOLIC_LINK);
    @SuppressWarnings("StaticCollection") // Set is immutable
    private static final Set<FileType> NO_TYPES = Set.of();

    private final Path path;
    private final FileEntry distinct;
    private final Lazy<FileAttributes> lazyAttributes_;
    private final Lazy<FileType> lazyType;

    private final List<Exception> problems = new LinkedList<>();
    private final Lazy<Attributes> lazyAttributes;

    private FileEntry(final Path path, final Normality normality, final FileEntry distinct, final LinkOption[] options) {
        this.path = normality.apply(path);
        this.distinct = distinct;
        this.lazyAttributes_ = Lazy.init(this::newAttributes_);
        this.lazyType = Lazy.init(this::newType);

        this.lazyAttributes = Lazy.init(() -> newAttributes(options));
    }

    /**
     * Returns a new {@link FileEntry} based on a given {@link Path}.
     */
    public static FileEntry of(final Path path) {
        return new FileEntry(path, Normality.UNKNOWN, null, DISTINCTIVE);
    }

    private Attributes newAttributes(final LinkOption[] options) {
        try {
            final BasicFileAttributes backing =
                    Files.readAttributes(path, BasicFileAttributes.class, options);
            if (backing.isDirectory()) {
                return new DirectoryAttributes(backing);
            } else {
                return new PlainAttributes(backing);
            }
        } catch (final IOException e) {
            problems.add(e);
            return new MissingFileAttributes();
        }
    }

    private Attributes attributes() {
        return lazyAttributes.get();
    }

    private FileAttributes newAttributes_() {
        try {
            final BasicFileAttributes backing =
                    Files.readAttributes(path, BasicFileAttributes.class, (null == distinct) ? DISTINCTIVE : RESOLVING);
            if (backing.isDirectory()) {
                return new DirectoryAttributes_(backing);
            } else {
                return new ExistingFileAttributes_(backing);
            }
        } catch (final IOException e) {
            // TODO?: problems.add(e);
            return new MissingFileAttributes_();
        }
    }

    private FileType newType() {
        return FileType.map(lazyAttributes_.get());
    }

    final boolean isDistinct() {
        return (null == distinct);
    }

    /**
     * Returns the file system path of the represented file as an
     * {@link Path#toAbsolutePath() absolute} {@link Path#normalize() normalized} {@link Path}.
     */
    public final Path path() {
        return path;
    }

    /**
     * Returns the simple name of the represented file.
     */
    public final String name() {
        return Optional.ofNullable(path.getFileName()).orElse(path).toString();
    }

    /**
     * Returns a {@link Set} of {@link FileType}s that correspond to the represented file.
     * A normal file corresponds to exactly one {@link FileType}.
     * <p>
     * If the result contains {@link FileType#SYMBOLIC_LINK},
     * it typically contains another {@link FileType} that describes the (ultimately) linked file.
     * <p>
     * If the result contains only {@link FileType#SYMBOLIC_LINK},
     * the represented file is a broken link. This means that the linked file does not exist.
     * <p>
     * If the result is empty, the file does not exist at all.
     */
    public final Set<FileType> types() {
        return attributes().types();
    }

    /**
     * Determines if the represented file is a directory.
     * This is especially the case if {@link #types()} contains {@link FileType#DIRECTORY}.
     */
    public final boolean isDirectory() {
        return types().contains(FileType.DIRECTORY);
    }

    /**
     * Determines if the represented file is a regular file.
     * This is especially the case if {@link #types()} contains {@link FileType#REGULAR_FILE}.
     */
    public final boolean isRegularFile() {
        return types().contains(FileType.REGULAR_FILE);
    }

    /**
     * Determines if the represented file is a symbolic link.
     * This is especially the case if {@link #types()} contains {@link FileType#SYMBOLIC_LINK}.
     */
    public final boolean isSymbolicLink() {
        return types().contains(FileType.SYMBOLIC_LINK);
    }

    /**
     * Determines if the represented file is something else than a directory, a regular file or a symbolic link.
     * Typically, a <em>device</em>.
     * This is especially the case if {@link #types()} contains {@link FileType#SPECIAL}.
     */
    public final boolean isSpecial() {
        return types().contains(FileType.SPECIAL);
    }

    /**
     * Determines if the represented file is actually missing.
     * This is especially the case if {@link #types()} is empty or contains only {@link FileType#SYMBOLIC_LINK}.
     */
    public final boolean isBroken() {
        return types().isEmpty() || types().equals(BROKEN_LINK);
    }

    /**
     * Determines if the represented file is physically missing.
     * This is especially the case if {@link #types()} is empty.
     */
    public final boolean isMissing() {
        return types().isEmpty();
    }

    /**
     * Returns the physical timestamp of the last modification of the represented file as an {@link Instant}.
     *
     * @throws UnsupportedOperationException if the file does not exist.
     */
    public final Instant lastModified() {
        return attributes().lastModified();
    }

    /**
     * Returns the effective timestamp of the last update of the represented file as an {@link Instant}.
     * <p>
     * If <em>this</em> {@link #isSymbolicLink()} returns the effective timestamp of the linked file.*
     * <p>
     * Else if <em>this</em> {@link #isDirectory()} returns the latest effective timestamp of the directory's content.**
     * <p>
     * Else returns the same value as {@link #lastModified()}.
     * <p>
     * If (*) <em>this</em> {@link #isBroken()} or if (**) a directory is empty returns {@code null}.
     *
     * @throws UnsupportedOperationException if the file does not exist.
     */
    public final Instant lastUpdated() {
        if (isSymbolicLink()) {
            throw new UnsupportedOperationException("not yet implemented");
        } else if (isDirectory()) {
            return entries().map(FileEntry::lastUpdated)
                            .filter(Objects::nonNull)
                            .reduce((left, right) -> (left.compareTo(right) < 0) ? right : left)
                            .orElse(null);
        } else {
            return lastModified();
        }
    }

    /**
     * Returns the timestamp of the last access to the represented file.
     *
     * @throws UnsupportedOperationException if the file does not exist.
     */
    public final Instant lastAccess() {
        return attributes().lastAccess();
    }

    /**
     * Returns the timestamp of the creation of the represented file.
     *
     * @throws UnsupportedOperationException if the file does not exist.
     */
    public final Instant creation() {
        return attributes().creation();
    }

    /**
     * Returns the physical size of the represented file.
     *
     * @throws UnsupportedOperationException if the file does not exist.
     */
    public final long size() {
        return attributes().size();
    }

    /**
     * Returns the effective size of the represented file.
     * <p>
     * If <em>this</em> {@link #isSymbolicLink()} returns the effective size of the linked file.*
     * <p>
     * Else if <em>this</em> {@link #isDirectory()} returns the summarized effective size of the directory's content.**
     * <p>
     * Else returns the same value as {@link #size()}.
     * <p>
     * If (*) <em>this</em> {@link #isBroken()} or if (**) a directory is empty returns {@code zero}.
     *
     * @throws UnsupportedOperationException if the file does not exist.
     */
    public final long effectiveSize() {
        if (isSymbolicLink()) {
            throw new UnsupportedOperationException("not yet implemented");
        } else if (isDirectory()) {
            return entries().map(FileEntry::effectiveSize)
                            .reduce(0L, Long::sum);
        } else {
            return size();
        }
    }

    /**
     * Returns the content of the represented file if it {@link #isDirectory() is a directory}.
     *
     * @throws UnsupportedOperationException if the represented file is not a directory.
     */
    public final Stream<FileEntry> entries() {
        return attributes().entries();
    }

    @Override
    public final String toString() {
        return path.toString();
    }

    private interface FileAttributes extends BasicFileAttributes {

        Path path();

        default Stream<FileEntry> entries() {
            throw new UnsupportedOperationException(format(ENTRIES_NOT_AVAILABLE, path()));
        }
    }

    private abstract static class Attributes {

        abstract Set<FileType> types();

        abstract long size();

        abstract Stream<FileEntry> entries();

        abstract Instant lastAccess();

        abstract Instant creation();

        abstract Instant lastModified();
    }

    private abstract static class ExistingFileAttributes extends Attributes {

        private final BasicFileAttributes backing;

        private ExistingFileAttributes(final BasicFileAttributes backing) {
            this.backing = backing;
        }

        @Override
        final Set<FileType> types() {
            return FileType.matching(backing);
        }

        @Override
        final long size() {
            return backing.size();
        }

        @Override
        final Instant lastAccess() {
            return backing.lastAccessTime().toInstant();
        }

        @Override
        final Instant creation() {
            return backing.creationTime().toInstant();
        }

        @Override
        final Instant lastModified() {
            return backing.lastModifiedTime().toInstant();
        }
    }

    private class MissingFileAttributes_ implements FileAttributes {

        @Override
        public final Path path() {
            return path;
        }

        @Override
        public final FileTime lastModifiedTime() {
            throw new UnsupportedOperationException(format(PROPERTY_NOT_AVAILABLE, path));
        }

        @Override
        public final FileTime lastAccessTime() {
            throw new UnsupportedOperationException(format(PROPERTY_NOT_AVAILABLE, path));
        }

        @Override
        public final FileTime creationTime() {
            throw new UnsupportedOperationException(format(PROPERTY_NOT_AVAILABLE, path));
        }

        @Override
        public final boolean isRegularFile() {
            return false;
        }

        @Override
        public final boolean isDirectory() {
            return false;
        }

        @Override
        public final boolean isSymbolicLink() {
            return false;
        }

        @Override
        public final boolean isOther() {
            return false;
        }

        @Override
        public final long size() {
            throw new UnsupportedOperationException(format(PROPERTY_NOT_AVAILABLE, path));
        }

        @Override
        public final Object fileKey() {
            throw new UnsupportedOperationException(format(PROPERTY_NOT_AVAILABLE, path));
        }
    }

    private class ExistingFileAttributes_ implements FileAttributes {

        private final BasicFileAttributes backing;

        ExistingFileAttributes_(final BasicFileAttributes backing) {
            this.backing = backing;
        }

        @Override
        public final Path path() {
            return path;
        }

        @Override
        public final FileTime lastModifiedTime() {
            return backing.lastModifiedTime();
        }

        @Override
        public final FileTime lastAccessTime() {
            return backing.lastAccessTime();
        }

        @Override
        public final FileTime creationTime() {
            return backing.creationTime();
        }

        @Override
        public final boolean isRegularFile() {
            return backing.isRegularFile();
        }

        @Override
        public final boolean isDirectory() {
            return backing.isDirectory();
        }

        @Override
        public final boolean isSymbolicLink() {
            return backing.isSymbolicLink();
        }

        @Override
        public final boolean isOther() {
            return backing.isOther();
        }

        @Override
        public final long size() {
            return backing.size();
        }

        @Override
        public final Object fileKey() {
            return backing.fileKey();
        }
    }

    private class DirectoryAttributes_ extends ExistingFileAttributes_ {

        private final Lazy<Set<FileEntry>> entrySet = null;

        DirectoryAttributes_(final BasicFileAttributes backing) {
            super(backing);
//            this.entrySet = Lazy.init(() -> {
//                try (final Stream<Path> stream = Files.list(path())) {
//                    return stream.map(path -> new FileEntry(path, Normality.DEFINITE, null))
//                                 .map(entry -> isDistinct() ? entry : entry.resolved())
//                                 .collect(Collectors.toCollection(() -> new TreeSet<>(ENTRY_ORDER)));
//                } catch (final IOException caught) {
//                    // TODO?: problems.add(caught);
//                    return Collections.emptySet();
//                }
//            });
        }

        @Override
        public Stream<FileEntry> entries() {
            return entrySet.get().stream();
        }
    }

    private final class DirectoryAttributes extends ExistingFileAttributes {

        private final Lazy<Set<FileEntry>> lazyEntries;

        private DirectoryAttributes(final BasicFileAttributes backing) {
            super(backing);
            this.lazyEntries = Lazy.init(this::newEntries);
        }

        private Set<FileEntry> newEntries() {
            try (final Stream<Path> stream = Files.list(path)) {
                return stream.map(childPath -> new FileEntry(childPath, Normality.DEFINITE, null, DISTINCTIVE))
                             .collect(Collectors.toCollection(() -> new TreeSet<>(ENTRY_ORDER)));
            } catch (final IOException caught) {
                problems.add(caught);
                return Collections.emptySet();
            }
        }

        @Override
        final Stream<FileEntry> entries() {
            return lazyEntries.get().stream();
        }
    }

    private final class PlainAttributes extends ExistingFileAttributes {

        private PlainAttributes(final BasicFileAttributes backing) {
            super(backing);
        }

        @Override
        final Stream<FileEntry> entries() {
            throw new UnsupportedOperationException(format(PROPERTY_NOT_AVAILABLE, path));
        }
    }

    private class MissingFileAttributes extends Attributes {

        @Override
        final Set<FileType> types() {
            return NO_TYPES;
        }

        @Override
        final long size() {
            throw new UnsupportedOperationException(format(PROPERTY_NOT_AVAILABLE, path));
        }

        @Override
        final Stream<FileEntry> entries() {
            throw new UnsupportedOperationException(format(PROPERTY_NOT_AVAILABLE, path));
        }

        @Override
        final Instant lastAccess() {
            throw new UnsupportedOperationException(format(PROPERTY_NOT_AVAILABLE, path));
        }

        @Override
        final Instant creation() {
            throw new UnsupportedOperationException(format(PROPERTY_NOT_AVAILABLE, path));
        }

        @Override
        final Instant lastModified() {
            throw new UnsupportedOperationException(format(PROPERTY_NOT_AVAILABLE, path));
        }
    }
}
