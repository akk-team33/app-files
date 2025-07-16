package de.team33.files.ui;

import de.team33.sphinx.alpha.visual.JFrames;
import net.team33.fscalc.ui.MainFrame;
import net.team33.fscalc.work.Context;

import javax.swing.*;
import java.util.function.Consumer;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class FilesFrame {

    private static final String TITLE = "Files";
    private static final String TITLE_FMT = "%s - " + TITLE;

    private static void onSetCWD(final JFrame jFrame, final Context.MsgChDir message) {
        jFrame.setTitle(TITLE_FMT.formatted(message.getPath().getName()));
    }

    public static JFrame show(final Context context) {
        return JFrames.builder()
                      .setTitle(TITLE)
                      .setDefaultCloseOperation(DISPOSE_ON_CLOSE)
                      .setContentPane(new MainFrame.ContentPane(context))
                      .setLocationByPlatform(true)
                      .pack()
                      .setup(jFrame -> context.getRegister()
                                              .add(new Listener(jFrame)))
                      .setVisible(true)
                      .build();
    }

    private record Listener(JFrame frame) implements Consumer<Context.MsgChDir> {

        @Override
        public final void accept(final Context.MsgChDir message) {
            frame.setTitle(TITLE_FMT.formatted(message.getPath().getName()));
        }
    }
}
