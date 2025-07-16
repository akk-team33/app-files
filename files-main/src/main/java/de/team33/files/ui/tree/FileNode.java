package de.team33.files.ui.tree;

import de.team33.files.ui.FileTreeModel;
import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.lazy.narvi.Lazy;

import java.nio.file.Path;
import java.util.List;

public class FileNode implements FileTreeModel.Node {

    private final FileEntry entry;
    private final Lazy<List<FileTreeModel.Node>> children;

    public FileNode(final FileEntry entry) {
        this.entry = entry;
        this.children = Lazy.init(() -> this.entry.entries()
                                                  .map(FileNode::new)
                                                  .map(FileTreeModel.Node.class::cast)
                                                  .toList());
    }

    @Override
    public final FileTreeModel.Node child(final int index) {
        return children.get().get(index);
    }

    @Override
    public final int childCount() {
        return children.get().size();
    }

    @Override
    public final boolean isLeaf() {
        return !entry.isDirectory();
    }

    @Override
    public final int indexOf(final FileTreeModel.Node child) {
        return children.get().indexOf(child);
    }

    @Override
    public final Path path() {
        return entry.path();
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof final FileNode node) && entry.path().equals(node.entry.path()));
    }

    @Override
    public final int hashCode() {
        return entry.path().hashCode();
    }

    @Override
    public final String toString() {
        return entry.path().toString();
    }
}
