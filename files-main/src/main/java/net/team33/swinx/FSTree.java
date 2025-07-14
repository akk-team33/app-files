package net.team33.swinx;

import javax.swing.*;
import java.io.File;

public class FSTree extends JTree {
    public FSTree() {
        super(new FSTreeModel());
        setRootVisible(false);
        setShowsRootHandles(true);
        setScrollsOnExpand(true);
        setExpandsSelectedPaths(true);
        getSelectionModel().setSelectionMode(1);
    }

    @Override
    public final FSTreeModel getModel() {
        return (FSTreeModel)super.getModel();
    }

    public final File getSelection() {
        return getModel().getFile(getSelectionPath());
    }

    public final void setSelection(final File f) {
        setSelectionPath(getModel().getTreePath(f));
    }
}
