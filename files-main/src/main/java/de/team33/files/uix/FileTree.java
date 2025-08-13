package de.team33.files.uix;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.lazy.narvi.Lazy;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.alpha.activity.Event;
import de.team33.sphinx.alpha.visual.JTrees;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

public final class FileTree {

    private final Variable<Path> path;
    private final JTree tree;
    private final JScrollPane pane;

    @SuppressWarnings("BoundedWildcard")
    private FileTree(final Variable<Path> path) {
        this.path = path;
        this.tree = JTrees.builder()
                          .setModel(new Model())
                          .setRootVisible(false)
                          .setShowsRootHandles(true)
                          .setScrollsOnExpand(true)
                          .setExpandsSelectedPaths(true)
                          .setup(FileTree::singleTreeSelection)
                          .on(Event.TREE_VALUE_CHANGED, this::onTreeValueChanged)
                          .on(Event.ANCESTOR_ADDED, this::onAncestorAdded)
                          .build();
        this.pane = new JScrollPane(tree);
        path.retrieve(this::setTreePath);
    }

    private static void singleTreeSelection(final JTree tree) {
        tree.getSelectionModel()
            .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    private static Path map(final TreePath treePath) {
        return ((Node) treePath.getLastPathComponent()).path();
    }

    private static TreePath map(final Path path) {
        return (null == path) ? new TreePath(Node.root())
                              : map(path.getParent()).pathByAddingChild(Node.file(path));
    }

    public static FileTree serving(final Variable<Path> path) {
        return new FileTree(path);
    }

    private void onAncestorAdded(final AncestorEvent event) {
        if (event.getSource() == tree) {
            tree.scrollPathToVisible(tree.getSelectionPath());
        } else {
            throw new IllegalStateException(event.toString());
        }
    }

    private void onTreeValueChanged(final TreeSelectionEvent event) {
        path.set(map(event.getPath()));
    }

    private void setTreePath(final Path path) {
        final TreePath treePath = map(path);
        tree.setSelectionPath(treePath);
        tree.scrollPathToVisible(treePath);
    }

    public final Component component() {
        return pane;
    }

    private static class Model implements TreeModel {

        private static Node node(final Object object) {
            return (Node) object;
        }

        @Override
        public final Node getRoot() {
            return Node.root();
        }

        @Override
        public final Node getChild(final Object parent, final int index) {
            return node(parent).children()
                               .get(index);
        }

        @Override
        public final int getChildCount(final Object parent) {
            return node(parent).children()
                               .size();
        }

        @Override
        public final boolean isLeaf(final Object node) {
            return node(node).isLeaf();
        }

        @Override
        public final void valueForPathChanged(final TreePath path, final Object newValue) {
            throw new UnsupportedOperationException("not yet implemented");
        }

        @Override
        public final int getIndexOfChild(final Object parent, final Object child) {
            return node(parent).children()
                               .indexOf(node(child));
        }

        @Override
        public final void addTreeModelListener(final TreeModelListener listener) {
            // TODO?
            // throw new UnsupportedOperationException("not yet implemented");
        }

        @Override
        public final void removeTreeModelListener(final TreeModelListener listener) {
            // TODO?
            // throw new UnsupportedOperationException("not yet implemented");
        }
    }

    private abstract static class Node {

        private static final Supplier<List<Node>> INIT_ROOT =
                () -> StreamSupport.stream(FileSystems.getDefault().getRootDirectories().spliterator(), false)
                                   .sorted(Comparator.comparing(Path::toString))
                                   .map(Node::file)
                                   .toList();

        private final Lazy<List<Node>> children;

        private Node(final Supplier<List<Node>> supplier) {
            this.children = Lazy.init(supplier);
        }

        static Node root() {
            return new Node.Root();
        }

        static Node file(final Path path) {
            return new Node.File(FileEntry.of(path).resolved());
        }

        final List<Node> children() {
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

        private static final class Root extends Node {

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

        private static final class File extends Node {

            private final FileEntry entry;

            private File(final FileEntry entry) {
                super(() -> entry.entries()
                                 .map(Node.File::new)
                                 .map(Node.class::cast)
                                 .toList());
                this.entry = entry;
            }

            @Override
            final boolean isLeaf() {
                return !entry.isDirectory();
            }

            @Override
            public final boolean equals(final Object obj) {
                return (this == obj) || ((obj instanceof final Node.File file) && entry.path()
                                                                                       .equals(file.entry.path()));
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
}
