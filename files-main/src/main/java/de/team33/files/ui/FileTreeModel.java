package de.team33.files.ui;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

class FileTreeModel implements TreeModel {

    private static FileTreeNode node(final Object object) {
        return (FileTreeNode) object;
    }

    @Override
    public final FileTreeNode getRoot() {
        return FileTreeNode.root();
    }

    @Override
    public final FileTreeNode getChild(final Object parent, final int index) {
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
    public final void addTreeModelListener(final TreeModelListener l) {
        // TODO?
        // throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public final void removeTreeModelListener(final TreeModelListener l) {
        // TODO?
        // throw new UnsupportedOperationException("not yet implemented");
    }
}
