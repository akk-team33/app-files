package de.team33.files.ui;

import de.team33.files.ui.tree.RootNode;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.nio.file.Path;

public class FileTreeModel implements TreeModel {

    private static final System.Logger LOG = System.getLogger(FileTreeModel.class.getCanonicalName());
    private static final String NULL_TYPE_NAME = "<null>";
    private static final String TYPE_MISMATCH = "type of <parent> is expected to be %s - but was %s";

    private final Node root = new RootNode();

    private static String typeName(final Object subject) {
        return (null == subject) ? NULL_TYPE_NAME : subject.getClass().getCanonicalName();
    }

    private static Node node(final Object subject) {
        if (subject instanceof final Node node) {
            return node;
        } else {
            throw new IllegalArgumentException(TYPE_MISMATCH.formatted(Node.class.getCanonicalName(),
                                                                       typeName(subject)));
        }
    }

    @Override
    public final Node getRoot() {
        return root;
    }

    @Override
    public final Node getChild(final Object parent, final int index) {
        return node(parent).child(index);
    }

    @Override
    public final int getChildCount(final Object parent) {
        return node(parent).childCount();
    }

    @Override
    public final boolean isLeaf(final Object node) {
        return node(node).isLeaf();
    }

    @Override
    public final void valueForPathChanged(final TreePath path, final Object newValue) {
        LOG.log(System.Logger.Level.INFO,
                () -> ("unexpected call: valueForPathChanged(path, newValue)%n" +
                       "    path:           %s%n" +
                       "    newValue:       %s%n" +
                       "    type(newValue): %s%n").formatted(path, newValue, typeName(newValue)));
    }

    @Override
    public final int getIndexOfChild(final Object parent, final Object child) {
        return node(parent).indexOf(node(child));
    }

    @Override
    public final void addTreeModelListener(final TreeModelListener listener) {
        LOG.log(System.Logger.Level.INFO,
                () -> ("unexpected call: addTreeModelListener(listener)%n" +
                       "    listener:       %s%n" +
                       "    type(listener): %s%n").formatted(listener, typeName(listener)));
    }

    @Override
    public final void removeTreeModelListener(final TreeModelListener listener) {
        LOG.log(System.Logger.Level.INFO,
                () -> ("unexpected call: removeTreeModelListener(listener)%n" +
                       "    listener:       %s%n" +
                       "    type(listener): %s%n").formatted(listener, typeName(listener)));
    }

    public File getFile(final TreePath path) {
        return ((Node) (path.getLastPathComponent())).path().toFile();
    }

    public TreePath getTreePath(final File file) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public interface Node {

        Node child(int index);

        int childCount();

        boolean isLeaf();

        int indexOf(Node child);

        Path path();
    }
}
