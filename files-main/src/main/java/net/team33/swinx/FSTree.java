package net.team33.swinx;

import javax.swing.*;
import java.nio.file.Path;

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

    public final Path getPath() {
        return getModel().getFile(getSelectionPath()).toPath();
    }

    public final void setPath(final Path path) {
        setSelectionPath(getModel().getTreePath(path.toFile()));
    }
}
