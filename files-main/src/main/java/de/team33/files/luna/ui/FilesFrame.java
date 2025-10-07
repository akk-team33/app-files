package de.team33.files.luna.ui;

import de.team33.sphinx.metis.JFrames;
import de.team33.sphinx.metis.JSplitPanes;

import javax.swing.*;
import java.awt.*;

public class FilesFrame {

    private final JFrame frame;

    private FilesFrame(final Context context) {
        this.frame = JFrames.builder()
                            .setLayout(new BorderLayout())
                            .add(CWDInput.by(context).ui(), BorderLayout.PAGE_START)
                            .add(JSplitPanes.builder()
                                            .setLeftComponent(FileTree.by(context).ui())
                                            .setRightComponent(FileTablePanel.by(context).ui())
                                            .build(), BorderLayout.CENTER)
                            .add(FilesStatus.by(context).ui(), BorderLayout.PAGE_END)
                            .build();
        // context.cwd().subscribe(INIT, this::onSetSWD);
    }

    public static FilesFrame by(final Context context) {
        return new FilesFrame(context);
    }

    public final JFrame ui() {
        return frame;
    }

    public interface Context extends CWDInput.Context, FileTree.Context, FileTablePanel.Context, FilesStatus.Context {

    }
}
