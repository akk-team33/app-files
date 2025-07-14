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

    @Override
    public final FSTreeModel getModel() {
        return (FSTreeModel)super.getModel();
    }

    public final File getSelection() {
        return this.getModel().getFile(this.getSelectionPath());
    }

    public final void setSelection(File f) {
        this.setSelectionPath(this.getModel().getTreePath(f));
    }
}
