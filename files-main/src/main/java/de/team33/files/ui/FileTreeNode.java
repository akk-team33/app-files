package de.team33.files.ui;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.lazy.narvi.Lazy;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

abstract class FileTreeNode {

    private static final Supplier<List<FileTreeNode>> INIT_ROOT =
            () -> StreamSupport.stream(FileSystems.getDefault().getRootDirectories().spliterator(), false)
                               .sorted(Comparator.comparing(Path::toString))
                               .map(FileTreeNode::file)
                               .toList();

    private final Lazy<List<FileTreeNode>> children;

    private FileTreeNode(final Supplier<List<FileTreeNode>> supplier) {
        this.children = Lazy.init(supplier);
    }

    static FileTreeNode root() {
        return new Root();
    }

    static FileTreeNode file(final Path path) {
        return new File(FileEntry.of(path).resolved());
    }

    final List<FileTreeNode> children() {
        return children.get();
    }

    abstract boolean isLeaf();

    @Override
    public abstract boolean equals(final Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

    abstract Path path();

    private static final class Root extends FileTreeNode {

        private Root() {
            super(INIT_ROOT);
        }

        @Override
        final boolean isLeaf() {
            return false;
        }

        @Override
        public final boolean equals(final Object obj) {
            return getClass() == obj.getClass();
        }

        @Override
        public final int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public final String toString() {
            return getClass().getCanonicalName();
        }

        @Override
        final Path path() {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }

    private static final class File extends FileTreeNode {

        private final FileEntry entry;

        private File(final FileEntry entry) {
            super(() -> entry.entries()
                             .map(File::new)
                             .map(FileTreeNode.class::cast)
                             .toList());
            this.entry = entry;
        }

        @Override
        final boolean isLeaf() {
            return !entry.isDirectory();
        }

        @Override
        public final boolean equals(final Object obj) {
            return (this == obj) || ((obj instanceof final File file) && entry.path().equals(file.entry.path()));
        }

        @Override
        public final int hashCode() {
            return entry.path().hashCode();
        }

        @Override
        public final String toString() {
            return entry.name();
        }

        @Override
        final Path path() {
            return entry.path();
        }
    }
}
