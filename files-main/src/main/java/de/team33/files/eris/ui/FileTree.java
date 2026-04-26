package de.team33.files.eris.ui;

import de.team33.files.luna.order.EntryOrder;
import de.team33.files.luna.ui.FilesIcons;
import de.team33.patterns.expiry.tethys.Recent;
import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.patterns.io.adrastea.LinkHandling;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.luna.Channel;
import de.team33.sphinx.metis.JTrees;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeCellRenderer;
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

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

public final class FileTree {

    private static final Node ROOT_NODE = new RootNode();

    private final Variable<Path> cwd;
    private final JTree tree;
    private final JScrollPane pane;

    private FileTree(final Context context) {
        this.cwd = context.cwd();
        this.tree = JTrees.builder()
                          .setModel(new Model())
                          .setCellRenderer(new CellRenderer(context.icons()))
                          .setRootVisible(false)
                          .setShowsRootHandles(true)
                          .setScrollsOnExpand(true)
                          .setExpandsSelectedPaths(true)
                          .setup(FileTree::singleTreeSelection)
                          .subscribe(Channel.TREE_VALUE_CHANGED, this::onTreeValueChanged)
                          .subscribe(Channel.ANCESTOR_ADDED, this::onAncestorAdded)
                          .build();
        this.pane = new JScrollPane(tree);
        cwd.subscribe(INIT, this::setTreePath);
    }

    private static void singleTreeSelection(final JTree tree) {
        tree.getSelectionModel()
            .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    private static Path map(final TreePath treePath) {
        return ((Node) treePath.getLastPathComponent()).path();
    }

    private static TreePath map(final Path path) {
        return (null == path) ? new TreePath(ROOT_NODE)
                              : map(path.getParent()).pathByAddingChild(FileNode.of(path));
    }

    public static FileTree by(final Context context) {
        return new FileTree(context);
    }

    private void onAncestorAdded(final AncestorEvent event) {
        if (event.getSource() == tree) {
            tree.scrollPathToVisible(tree.getSelectionPath());
        } else {
            throw new IllegalStateException(event.toString());
        }
    }

    private void onTreeValueChanged(final TreeSelectionEvent event) {
        cwd.set(map(event.getPath()));
    }

    private void setTreePath(final Path path) {
        final TreePath treePath = map(path);
        tree.setSelectionPath(treePath);
        tree.scrollPathToVisible(treePath);
    }

    public final Component ui() {
        return pane;
    }

    @SuppressWarnings("InterfaceWithOnlyOneDirectInheritor")
    public interface Context {

        FilesIcons icons();

        Variable<Path> cwd();
    }

    private static class Model implements TreeModel {

        private static Node node(final Object object) {
            return (Node) object;
        }

        @Override
        public final Node getRoot() {
            return ROOT_NODE;
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
            throw new UnsupportedOperationException("unsupported operation");
        }

        @Override
        public final int getIndexOfChild(final Object parent, final Object child) {
            return node(parent).children()
                               .indexOf(node(child));
        }

        @Override
        public final void addTreeModelListener(final TreeModelListener listener) {
            // TODO?
        }

        @Override
        public final void removeTreeModelListener(final TreeModelListener listener) {
            // TODO?
        }
    }

    private abstract static class Node {

        private final Recent<List<Node>> children;

        private Node(final Supplier<List<Node>> supplier) {
            this.children = new Recent<>(supplier, 25, 250);
        }

        final List<Node> children() {
            return children.get();
        }

        abstract boolean isLeaf();

        abstract Path path();
    }

    private static final class RootNode extends Node {

        private RootNode() {
            super(() -> StreamSupport.stream(FileSystems.getDefault()
                                                        .getRootDirectories()
                                                        .spliterator(), false)
                                     .sorted(Comparator.comparing(Path::toString))
                                     .map(FileNode::of)
                                     .toList());
        }

        @Override
        final boolean isLeaf() {
            return false;
        }

        @Override
        final Path path() {
            throw new UnsupportedOperationException("unsupported operation");
        }

        @Override
        public final boolean equals(final Object obj) {
            return (this == obj) || (getClass() == obj.getClass());
        }

        @Override
        public final int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public final String toString() {
            return getClass().getCanonicalName();
        }
    }

    private static final class FileNode extends Node {

        private static final FileEntry.Lister LISTER = FileEntry.lister(LinkHandling.RESOLVE)
                                                                .noOrder()
                                                                .entryOrder(EntryOrder.BY_NAME);

        private final FileEntry entry;

        private FileNode(final FileEntry entry) {
            super(() -> LISTER.list(entry)
                              .stream()
                              .filter(FileEntry::isDirectory)
                              .map(FileNode::of)
                              .toList());
            this.entry = entry;
        }

        private static Node of(final Path path) {
            return new FileNode(FileEntry.resolved(path));
        }

        private static Node of(final FileEntry entry) {
            return new FileNode(entry);
        }

        @Override
        final boolean isLeaf() {
            return !entry.isDirectory();
        }

        @Override
        final Path path() {
            return entry.path();
        }

        @Override
        public final boolean equals(final Object obj) {
            return (this == obj) || ((obj instanceof final FileNode other) && entry.path()
                                                                                   .equals(other.entry.path()));
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

    private static final class CellRenderer extends DefaultTreeCellRenderer {

        @SuppressWarnings("AssignmentToSuperclassField")
        private CellRenderer(final FilesIcons icons) {
            this.closedIcon = icons.stdFolder();
            this.openIcon = icons.opnFolder();
            this.leafIcon = icons.stdFile();
        }
    }
}