package net.team33.swinx;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

public class FSTreeModel implements TreeModel {
    private static final NODE m_Root = new ROOTNODE();
    private static final FileFilter DIR_FILTER = new FileFilter() {
        @Override
        public boolean accept(final File f) {
            return f.isDirectory();
        }
    };
    private static final Comparator<NODE> NODE_COMPARATOR = new Comparator<NODE>() {
        @Override
        public int compare(final NODE n1, final NODE n2) {
            return n1.toString().compareToIgnoreCase(n2.toString());
        }
    };

    public final TreePath getTreePath(final File f) {
        return f == null ? new TreePath(m_Root) : getTreePath(f.getParentFile()).pathByAddingChild(getNode(f));
    }

    public final File getFile(final TreePath selectionPath) {
        return selectionPath == null ? null : ((NODE)selectionPath.getLastPathComponent()).getFile();
    }

    @Override
    public final Object getRoot() {
        return m_Root;
    }

    @Override
    public final Object getChild(final Object parent, final int index) {
        return ((NODE)parent).getChildren().get(index);
    }

    @Override
    public final int getChildCount(final Object parent) {
        return ((NODE)parent).getChildren().size();
    }

    @Override
    public final int getIndexOfChild(final Object parent, final Object child) {
        return ((NODE)parent).getChildren().indexOf(child);
    }

    @Override
    public final boolean isLeaf(final Object node) {
        return false;
    }

    @Override
    public void valueForPathChanged(final TreePath path, final Object newValue) {
    }

    @Override
    public synchronized void addTreeModelListener(final TreeModelListener l) {
    }

    @Override
    public synchronized void removeTreeModelListener(final TreeModelListener l) {
    }

    private static NODE getNode(final File f) {
        return f == null ? m_Root : getNode(f.getParentFile()).getChild(f);
    }

    private static class DIRNODE extends NODE {
        private final File m_File;

        public DIRNODE(final File f) {
            this.m_File = f;
        }

        public final String toString() {
            return m_File.getParentFile() == null ? m_File.getAbsolutePath() : m_File.getName();
        }

        @Override
        public final File getFile() {
            return m_File;
        }

        @Override
        protected final File[] getChildFiles() {
            return m_File.listFiles(FSTreeModel.DIR_FILTER);
        }
    }

    private abstract static class NODE {
        private Vector<NODE> m_Children;

        private NODE() {
            this.m_Children = null;
        }

        public final NODE getChild(File f) {
            try {
                f = f.getCanonicalFile();
                final Iterator var3 = getChildren().iterator();

                while(var3.hasNext()) {
                    final NODE child = (NODE)var3.next();
                    if (child.getFile().equals(f)) {
                        return child;
                    }
                }
            } catch (final IOException var4) {
            }

            return this;
        }

        public final Vector<NODE> getChildren() {
            if (m_Children == null) {
                final File[] f = getChildFiles();
                final Set<NODE> nodes = new TreeSet(FSTreeModel.NODE_COMPARATOR);
                if (f != null) {
                    for(int i = 0; i < f.length; ++i) {
                        nodes.add(new DIRNODE(f[i]));
                    }
                }

                this.m_Children = new Vector(nodes);
            }

            return m_Children;
        }

        public abstract File getFile();

        protected abstract File[] getChildFiles();
    }

    private static class ROOTNODE extends NODE {

        public final String toString() {
            return "Filesystem";
        }

        @Override
        public final File getFile() {
            return null;
        }

        @Override
        protected final File[] getChildFiles() {
            return File.listRoots();
        }
    }
}
