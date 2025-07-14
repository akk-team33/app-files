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
        public boolean accept(File f) {
            return f.isDirectory();
        }
    };
    private static final Comparator<NODE> NODE_COMPARATOR = new Comparator<NODE>() {
        @Override
        public int compare(NODE n1, NODE n2) {
            return n1.toString().compareToIgnoreCase(n2.toString());
        }
    };

    public final TreePath getTreePath(File f) {
        return f == null ? new TreePath(m_Root) : this.getTreePath(f.getParentFile()).pathByAddingChild(getNode(f));
    }

    public final File getFile(TreePath selectionPath) {
        return selectionPath == null ? null : ((NODE)selectionPath.getLastPathComponent()).getFile();
    }

    @Override
    public final Object getRoot() {
        return m_Root;
    }

    @Override
    public final Object getChild(Object parent, int index) {
        return ((NODE)parent).getChildren().get(index);
    }

    @Override
    public final int getChildCount(Object parent) {
        return ((NODE)parent).getChildren().size();
    }

    @Override
    public final int getIndexOfChild(Object parent, Object child) {
        return ((NODE)parent).getChildren().indexOf(child);
    }

    @Override
    public final boolean isLeaf(Object node) {
        return false;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public synchronized void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public synchronized void removeTreeModelListener(TreeModelListener l) {
    }

    private static NODE getNode(File f) {
        return f == null ? m_Root : getNode(f.getParentFile()).getChild(f);
    }

    private static class DIRNODE extends NODE {
        private final File m_File;

        public DIRNODE(File f) {
            this.m_File = f;
        }

        public final String toString() {
            return this.m_File.getParentFile() == null ? this.m_File.getAbsolutePath() : this.m_File.getName();
        }

        @Override
        public final File getFile() {
            return this.m_File;
        }

        @Override
        protected final File[] getChildFiles() {
            return this.m_File.listFiles(FSTreeModel.DIR_FILTER);
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
                Iterator var3 = this.getChildren().iterator();

                while(var3.hasNext()) {
                    NODE child = (NODE)var3.next();
                    if (child.getFile().equals(f)) {
                        return child;
                    }
                }
            } catch (IOException var4) {
            }

            return this;
        }

        public final Vector<NODE> getChildren() {
            if (this.m_Children == null) {
                File[] f = this.getChildFiles();
                Set<NODE> nodes = new TreeSet(FSTreeModel.NODE_COMPARATOR);
                if (f != null) {
                    for(int i = 0; i < f.length; ++i) {
                        nodes.add(new DIRNODE(f[i]));
                    }
                }

                this.m_Children = new Vector(nodes);
            }

            return this.m_Children;
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
