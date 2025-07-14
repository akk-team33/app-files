package net.team33.fscalc.ui;

import de.team33.sphinx.alpha.visual.JFrames;
import net.team33.fscalc.ui.rsrc.Ico;
import net.team33.fscalc.work.Context;
import net.team33.swinx.FSTree;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.function.Consumer;

public abstract class MainFrame extends JFrame {
    private static final String TTL_SUFFIX = "FSCalc";
    private static final String TTL_FORMAT = "%s - FSCalc";

    protected abstract Context getContext();

    protected MainFrame() {
        super("FSCalc");
        setDefaultCloseOperation(2);
        setContentPane(new ContentPane());
        pack();
        setLocationByPlatform(true);
        getContext().getRegister().add(new ADAPTER());
    }

    public static MainFrame by(final Context context) {
        return JFrames.builder(() -> new MainFrame() {
                          @Override
                          protected Context getContext() {
                              return context;
                          }
                      })
                      .setTitle(TTL_SUFFIX)
                      .build();
    }

    private class ADAPTER implements Consumer<Context.MsgChDir> {

        @Override
        public final void accept(final Context.MsgChDir message) {
            setTitle(String.format("%s - FSCalc", message.getPath().getName()));
        }
    }

    private class CenterPane extends JSplitPane {
        private CenterPane() {
            setLeftComponent(MainFrame.this.new LeftCenterPane());
            setRightComponent(MainFrame.this.new RghtCenterPane());
        }
    }

    private class ContentPane extends JPanel {
        private ContentPane() {
            super(new BorderLayout());
            add(MainFrame.this.new NorthPane(), "North");
            add(MainFrame.this.new CenterPane(), "Center");
            add(new ProgressPane(getContext()), "South");
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
            setCellRenderer(new RENDERER());
            getContext().getRegister().add(new LSN_CHDIR());
            addTreeSelectionListener(new SEL_ADAPTER());
        }

        private class LSN_CHDIR implements Consumer<Context.MsgChDir> {

            @Override
            public final void accept(final Context.MsgChDir message) {
                setSelection(message.getPath());
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
            public final void valueChanged(final TreeSelectionEvent e) {
                final File f = getModel().getFile(e.getPath());
                getContext().setPath(f);
            }
        }
    }
}
