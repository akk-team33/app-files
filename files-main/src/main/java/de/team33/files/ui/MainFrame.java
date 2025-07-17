package de.team33.files.ui;

import de.team33.sphinx.alpha.activity.Event;
import de.team33.sphinx.alpha.visual.JPanels;
import de.team33.sphinx.alpha.visual.JSplitPanes;
import de.team33.sphinx.alpha.visual.JTrees;
import net.team33.fscalc.ui.BrowserPane;
import net.team33.fscalc.ui.PathPane;
import net.team33.fscalc.ui.ProgressPane;
import net.team33.fscalc.work.Context;
import net.team33.swinx.FSTree;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;

public class MainFrame extends JFrame implements Consumer<Context.MsgChDir> {
    private static final String TTL_SUFFIX = "FSCalc";
    private static final String TTL_FORMAT = "%s - FSCalc";

    private MainFrame(final Context context) {
        super(TTL_SUFFIX);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(contentPane(context));
        setLocationByPlatform(true);
        pack();
        context.getRegister().add(this);
    }

    public static MainFrame by(final Context context) {
        return new MainFrame(context);
    }

    private static JSplitPane centerPane(final Context context) {
        return JSplitPanes.builder()
                          .setLeftComponent(leftCenterPane(context))
                          .setRightComponent(rightCenterPane(context))
                          .build();
    }

    private static JPanel contentPane(final Context context) {
        //noinspection AbsoluteAlignmentInUserInterface
        return JPanels.builder()
                      .setLayout(new BorderLayout())
                      .add(northPane(context), BorderLayout.NORTH)
                      .add(centerPane(context), BorderLayout.CENTER)
                      .add(new ProgressPane(context), BorderLayout.SOUTH)
                      .build();
    }

    private static JScrollPane leftCenterPane(final Context context) {
        return new JScrollPane(treeView(context));
    }

    private static PathPane northPane(final Context context) {
        return new PathPane() {
            @Override
            protected Context getContext() {
                return context;
            }
        };
    }

    private static BrowserPane rightCenterPane(final Context context) {
        return new BrowserPane() {
            @Override
            protected Context getContext() {
                return context;
            }
        };
    }

    private static FSTree treeView(final Context context) {
        return JTrees.builder(FSTree::new)
                     .setCellRenderer(Basics.treeCellRenderer())
                     .setup(fsTree -> context.getRegister().add(fsTree))
                     .on(Event.TREE_VALUE_CHANGED, event -> onTreeValueChanged(event, context))
                     .build();
    }

    private static void onTreeValueChanged(final TreeSelectionEvent event, final Context context) {
        if (event.getSource() instanceof final FSTree fsTree) {
            context.setPath(fsTree.getModel().getFile(event.getPath()));
        } else {
            final String sourceType = Optional.ofNullable(event.getSource())
                                              .map(Object::getClass)
                                              .map(Class::getCanonicalName)
                                              .orElse("<null>");
            throw new IllegalStateException("event source is expected to be an <FSTree> - but was <" +
                                            sourceType +
                                            ">");
        }
    }

    @Override
    public final void accept(final Context.MsgChDir message) {
        setTitle(TTL_FORMAT.formatted(message.getPath().getName()));
    }
}
