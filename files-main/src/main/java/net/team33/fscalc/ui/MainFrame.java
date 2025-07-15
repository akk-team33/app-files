package net.team33.fscalc.ui;

import de.team33.sphinx.alpha.visual.JFrames;
import net.team33.fscalc.ui.rsrc.Ico;
import net.team33.fscalc.work.Context;
import net.team33.swinx.FSTree;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.function.Consumer;

public class MainFrame extends JFrame {
    private static final String TTL_SUFFIX = "FSCalc";
    private static final String TTL_FORMAT = "%s - FSCalc";

    public MainFrame(final Context context) {
        super("FSCalc");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(new ContentPane(context));
        setLocationByPlatform(true);
        pack();
        context.getRegister().add(new ADAPTER());
    }

    public static MainFrame by(final Context context) {
        return JFrames.builder(() -> new MainFrame(context))
                      .setTitle(TTL_SUFFIX)
                      .build();
    }

    private class ADAPTER implements Consumer<Context.MsgChDir> {

        @Override
        public final void accept(final Context.MsgChDir message) {
            setTitle(String.format("%s - FSCalc", message.getPath().getName()));
        }
    }

    private static JScrollPane leftCenterPane(final Context context) {
        return new JScrollPane(new TreeView(context));
    }

    private static PathPane northPane(final Context context) {
        return new PathPane(context);
    }

    private static BrowserPane rightCenterPane(final Context context) {
        return new BrowserPane(context);
    }

    private static class CenterPane extends JSplitPane {
        private CenterPane(final Context context) {
            setLeftComponent(leftCenterPane(context));
            setRightComponent(rightCenterPane(context));
        }
    }

    private static class ContentPane extends JPanel {
        private ContentPane(final Context context) {
            super(new BorderLayout());
            add(northPane(context), BorderLayout.NORTH);
            add(new CenterPane(context), BorderLayout.CENTER);
            add(new ProgressPane(context), BorderLayout.SOUTH);
        }
    }

    private static class TreeView extends FSTree {

        private TreeView(final Context context) {
            setCellRenderer(new RENDERER());
            context.getRegister().add(new LSN_CHDIR());
            addTreeSelectionListener(selAdapter(context));
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

        private TreeSelectionListener selAdapter(final Context context) {
            return e -> {
                final File f = getModel().getFile(e.getPath());
                context.setPath(f);
            };
        }
    }
}
