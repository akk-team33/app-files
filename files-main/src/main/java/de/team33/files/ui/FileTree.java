package de.team33.files.ui;

import de.team33.patterns.serving.alpha.Settable;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.alpha.activity.Event;
import de.team33.sphinx.alpha.visual.JTrees;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.nio.file.Path;

final class FileTree extends JTree {

    private FileTree(final Variable<Path> path) {
        super(new FileTreeModel());
    }

    static FileTree using(final Variable<Path> path) {
        return JTrees.builder(() -> new FileTree(path))
                     .setRootVisible(false)
                     .setShowsRootHandles(true)
                     .setScrollsOnExpand(true)
                     .setExpandsSelectedPaths(true)
                     .setup(tree -> tree.getSelectionModel()
                                        .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION))
                     .setup(tree -> path.retrieve(tree::setPath))
                     .on(Event.TREE_VALUE_CHANGED, event -> onTreeValueChanged(event, path))
                     .on(Event.ANCESTOR_ADDED, FileTree::onAncestorAdded)
                     .build();
    }

    private static void onAncestorAdded(final AncestorEvent event) {
        if (event.getSource() instanceof final FileTree tree) {
            tree.scrollPathToVisible(tree.getSelectionPath());
        } else {
            throw new IllegalStateException(event.toString());
        }
    }

    private static TreePath map(final Path path) {
        return (null == path) ? new TreePath(FileTreeNode.root())
                              : map(path.getParent()).pathByAddingChild(FileTreeNode.file(path));
    }

    private static Path map(final TreePath treePath) {
        return ((FileTreeNode) treePath.getLastPathComponent()).path();
    }

    private static void onTreeValueChanged(final TreeSelectionEvent event, final Settable<? super Path> path) {
        path.set(map(event.getPath()));
    }

    private void setPath(final Path path) {
        final TreePath treePath = map(path);
        setSelectionPath(treePath);
        scrollPathToVisible(treePath);
    }
}
