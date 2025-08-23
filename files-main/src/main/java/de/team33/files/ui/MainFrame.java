package de.team33.files.ui;

import de.team33.sphinx.alpha.visual.JPanels;
import de.team33.sphinx.alpha.visual.JSplitPanes;
import net.team33.fscalc.ui.PathPane;
import net.team33.fscalc.ui.ProgressPane;
import net.team33.fscalc.work.Context;

import javax.swing.*;
import java.awt.*;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

public class MainFrame extends JFrame {
    private static final String TTL_SUFFIX = "Files";
    private static final String TTL_FORMAT = "%s - " + TTL_SUFFIX;

    private MainFrame(final Context context) {
        super(TTL_SUFFIX);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(contentPane(context));
        setLocationByPlatform(true);
        pack();
        context.cwd().subscribe(INIT, path -> setTitle(TTL_FORMAT.formatted(path.getFileName())));
    }

    public static MainFrame by(final Context context) {
        return new MainFrame(context);
    }

    private static JSplitPane centerPane(final Context context) {
        return JSplitPanes.builder()
                          .setLeftComponent(leftCenterPane(context))
                          .setRightComponent(BrowserPane.by(context))
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

    private static Component leftCenterPane(final Context context) {
        return FileTree.by(context)
                       .component();
    }

    private static PathPane northPane(final Context context) {
        return new PathPane() {
            @Override
            protected Context getContext() {
                return context;
            }
        };
    }
}
