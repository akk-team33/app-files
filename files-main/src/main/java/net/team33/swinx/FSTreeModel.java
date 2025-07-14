//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.swinx;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

public class FSTreeModel implements TreeModel {
    private static NODE m_Root = new ROOTNODE();
    private static final FileFilter DIR_FILTER = new FileFilter() {
        public boolean accept(File f) {
            return f.isDirectory();
        }
    };
    private static final Comparator<NODE> NODE_COMPARATOR = new Comparator<NODE>() {
        public int compare(NODE n1, NODE n2) {
            return n1.toString().compareToIgnoreCase(n2.toString());
        }
    };

    public FSTreeModel() {
    }

    public TreePath getTreePath(File f) {
        return f == null ? new TreePath(m_Root) : this.getTreePath(f.getParentFile()).pathByAddingChild(getNode(f));
    }

    public File getFile(TreePath selectionPath) {
        return selectionPath == null ? null : ((NODE)selectionPath.getLastPathComponent()).getFile();
    }

    public Object getRoot() {
        return m_Root;
    }

    public Object getChild(Object parent, int index) {
        return ((NODE)parent).getChildren().get(index);
    }

    public int getChildCount(Object parent) {
        return ((NODE)parent).getChildren().size();
    }

    public int getIndexOfChild(Object parent, Object child) {
        return ((NODE)parent).getChildren().indexOf(child);
    }

    public boolean isLeaf(Object node) {
        return false;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public synchronized void addTreeModelListener(TreeModelListener l) {
    }

    public synchronized void removeTreeModelListener(TreeModelListener l) {
    }

    private static NODE getNode(File f) {
        return f == null ? m_Root : getNode(f.getParentFile()).getChild(f);
    }

    private static class DIRNODE extends NODE {
        private File m_File;

        public DIRNODE(File f) {
            this.m_File = f;
        }

        public String toString() {
            return this.m_File.getParentFile() == null ? this.m_File.getAbsolutePath() : this.m_File.getName();
        }

        public File getFile() {
            return this.m_File;
        }

        protected File[] getChildFiles() {
            return this.m_File.listFiles(FSTreeModel.DIR_FILTER);
        }
    }

    private abstract static class NODE {
        private Vector<NODE> m_Children;

        private NODE() {
            this.m_Children = null;
        }

        public NODE getChild(File f) {
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

        public Vector<NODE> getChildren() {
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

        public String toString() {
            return "Filesystem";
        }

        public File getFile() {
            return null;
        }

        protected File[] getChildFiles() {
            return File.listRoots();
        }
    }
}
