package de.team33.files.uix;

import de.team33.patterns.expiry.tethys.Recent;
import de.team33.patterns.io.phobos.FileEntry;

import javax.swing.tree.TreeNode;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.StreamSupport;

abstract class FileTreeNode implements TreeNode {

    static final TreeNode ROOT = new FileTreeNode(null) {

        @Override
        final List<? extends TreeNode> newChildren() {
            return StreamSupport.stream(FileSystems.getDefault().getRootDirectories().spliterator(), false)
                                .sorted(Comparator.comparing(Path::toString))
                                .map(FileEntry::of)
                                .map(FileEntry::resolved)
                                .map(e -> new EntryNode(this, e))
                                .toList();
        }

        @Override
        public boolean getAllowsChildren() {
            return true;
        }
    };

    private final TreeNode parent;
    private final Recent<? extends List<? extends TreeNode>> children;

    FileTreeNode(final TreeNode parent) {
        this.parent = parent;
        this.children = new Recent<>(this::newChildren, 25, 500);
    }

    abstract List<? extends TreeNode> newChildren();

    @Override
    public final TreeNode getChildAt(final int childIndex) {
        return children.get().get(childIndex);
    }

    @Override
    public final int getChildCount() {
        return children.get().size();
    }

    @Override
    public final TreeNode getParent() {
        return parent;
    }

    @Override
    public final int getIndex(final TreeNode node) {
        return children.get().indexOf(node);
    }

    @Override
    public final boolean isLeaf() {
        return !getAllowsChildren();
    }

    @Override
    public final Enumeration<? extends TreeNode> children() {
        return Collections.enumeration(children.get());
    }

    private static final class EntryNode extends FileTreeNode {

        private final FileEntry entry;

        private EntryNode(final TreeNode parent, final FileEntry entry) {
            super(parent);
            this.entry = entry;
        }

        @Override
        final List<? extends TreeNode> newChildren() {
            return entry.entries()
                        .filter(FileEntry::isDirectory) // Configurable?
                        .map(fileEntry -> new EntryNode(this, fileEntry))
                        .toList();
        }

        @Override
        public final boolean getAllowsChildren() {
            return entry.isDirectory();
        }

        @Override
        public final boolean equals(final Object obj) {
            return (this == obj) || ((obj instanceof final EntryNode node) && entry.path().equals(node.entry.path()));
        }

        @Override
        public final int hashCode() {
            return entry.path().hashCode();
        }

        @Override
        public final String toString() {
            return entry.name();
        }
    }
}
