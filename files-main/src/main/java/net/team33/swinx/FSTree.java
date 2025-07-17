package net.team33.swinx;

import net.team33.fscalc.work.Context;

import javax.swing.*;
import java.io.File;
import java.util.function.Consumer;

public class FSTree extends JTree implements Consumer<Context.MsgChDir> {
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

    @Override
    public final void accept(final Context.MsgChDir message) {
        setSelection(message.getPath());
    }
}
