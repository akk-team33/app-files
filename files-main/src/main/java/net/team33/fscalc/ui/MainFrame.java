package net.team33.fscalc.ui;

import net.team33.fscalc.ui.rsrc.Ico;
import net.team33.fscalc.work.Context;
import net.team33.messaging.Listener;
import net.team33.swinx.FSTree;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;

public abstract class MainFrame extends JFrame {
    private static final String TTL_SUFFIX = "FSCalc";
    private static final String TTL_FORMAT = "%s - FSCalc";

    protected abstract Context getContext();

    protected MainFrame() {
        super("FSCalc");
        this.setDefaultCloseOperation(2);
        this.setContentPane(new ContentPane());
        this.pack();
        this.setLocationByPlatform(true);
        this.getContext().getRegister().add(new ADAPTER());
    }

    private class ADAPTER implements Listener<Context.MsgChDir> {

        @Override
        public final void pass(Context.MsgChDir message) {
            MainFrame.this.setTitle(String.format("%s - FSCalc", message.getPath().getName()));
        }
    }

    private class CenterPane extends JSplitPane {
        private CenterPane() {
            this.setLeftComponent(MainFrame.this.new LeftCenterPane());
            this.setRightComponent(MainFrame.this.new RghtCenterPane());
        }
    }

    private class ContentPane extends JPanel {
        private ContentPane() {
            super(new BorderLayout());
            this.add(MainFrame.this.new NorthPane(), "North");
            this.add(MainFrame.this.new CenterPane(), "Center");
            this.add(new ProgressPane(MainFrame.this.getContext()), "South");
        }
    }

    private class LeftCenterPane extends JScrollPane {
        private LeftCenterPane() {
            super(MainFrame.this.new TreeView());
        }
    }

    private class NorthPane extends PathPane {

        @Override
        protected final Context getContext() {
            return MainFrame.this.getContext();
        }
    }

    private class RghtCenterPane extends BrowserPane {

        @Override
        protected final Context getContext() {
            return MainFrame.this.getContext();
        }
    }

    private class TreeView extends FSTree {
        private TreeView() {
            this.setCellRenderer(new RENDERER());
            MainFrame.this.getContext().getRegister().add(new LSN_CHDIR());
            this.addTreeSelectionListener(new SEL_ADAPTER());
        }

        private class LSN_CHDIR implements Listener<Context.MsgChDir> {

            @Override
            public final void pass(Context.MsgChDir message) {
                TreeView.this.setSelection(message.getPath());
            }
        }

        private class RENDERER extends DefaultTreeCellRenderer {
            private static final long serialVersionUID = 1220062910464484785L;

            private RENDERER() {
                this.closedIcon = Ico.CLSDIR;
                this.openIcon = Ico.OPNDIR;
                this.leafIcon = Ico.FILE;
            }
        }

        private class SEL_ADAPTER implements TreeSelectionListener {

            @Override
            public final void valueChanged(TreeSelectionEvent e) {
                File f = TreeView.this.getModel().getFile(e.getPath());
                MainFrame.this.getContext().setPath(f);
            }
        }
    }
}
