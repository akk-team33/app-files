//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.swinx;

import javax.swing.*;
import java.io.File;

public class FSTree extends JTree {
    public FSTree() {
        super(new FSTreeModel());
        this.setRootVisible(false);
        this.setShowsRootHandles(true);
        this.setScrollsOnExpand(true);
        this.setExpandsSelectedPaths(true);
        this.getSelectionModel().setSelectionMode(1);
    }

    public FSTreeModel getModel() {
        return (FSTreeModel)super.getModel();
    }

    public void setSelection(File f) {
        this.setSelectionPath(this.getModel().getTreePath(f));
    }

    public File getSelection() {
        return this.getModel().getFile(this.getSelectionPath());
    }
}
