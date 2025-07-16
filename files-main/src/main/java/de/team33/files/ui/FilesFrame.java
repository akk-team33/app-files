package de.team33.files.ui;

import de.team33.sphinx.alpha.visual.JFrames;
import de.team33.sphinx.alpha.visual.JPanels;
import de.team33.sphinx.alpha.visual.JSplitPanes;
import net.team33.fscalc.ui.PathPane;
import net.team33.fscalc.ui.ProgressPane;
import net.team33.fscalc.work.Context;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static net.team33.fscalc.ui.MainFrame.leftCenterPane;
import static net.team33.fscalc.ui.MainFrame.rightCenterPane;

public class FilesFrame extends JFrame implements Consumer<Context.MsgChDir> {

    private static final String TITLE = "Files";
    private static final String TITLE_FMT = "%s - " + TITLE;

    @SuppressWarnings("UnusedReturnValue")
    public static FilesFrame show(final Context context) {
        return JFrames.builder(FilesFrame::new)
                      .setTitle(TITLE)
                      .setDefaultCloseOperation(DISPOSE_ON_CLOSE)
                      .setContentPane(contentPane(context))
                      .setLocationByPlatform(true)
                      .pack()
                      .setup(frame -> context.getRegister().add(frame))
                      .setVisible(true)
                      .build();
    }

    private static JPanel contentPane(final Context context) {
        return JPanels.builder()
                      .setLayout(new BorderLayout())
                      .add(new PathPane(context), BorderLayout.PAGE_START)
                      .add(centerPane(context), BorderLayout.CENTER)
                      .add(new ProgressPane(context), BorderLayout.PAGE_END)
                      .build();
    }

    private static JSplitPane centerPane(final Context context) {
        return JSplitPanes.builder()
                          .setLeftComponent(leftCenterPane(context))
                          .setRightComponent(rightCenterPane(context))
                          .build();
    }

    @Override
    public final void accept(final Context.MsgChDir message) {
        setTitle(TITLE_FMT.formatted(message.getPath().getName()));
    }
}
