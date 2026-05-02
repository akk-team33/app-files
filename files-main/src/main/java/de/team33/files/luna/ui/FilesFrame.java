package de.team33.files.luna.ui;

import de.team33.files.luna.context.UIContext;
import de.team33.sphinx.metis.JFrames;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

public final class FilesFrame {

    private final JFrame frame;

    private FilesFrame(final UIContext context) {
        this.frame = JFrames.builder()
                            .setLayout(new BorderLayout())
                            .add(CWDInput.by(context).ui(), BorderLayout.PAGE_START)
                            .add(CenterPane.by(context).ui(), BorderLayout.CENTER)
                            .add(FilesStatus.by(context).ui(), BorderLayout.PAGE_END)
                            .build();
        context.cwd().subscribe(INIT, this::onSetCWD);
    }

    private void onSetCWD(final Path path) {
        frame.setTitle("%s - Files".formatted(path.getFileName()));
    }

    public static FilesFrame by(final UIContext context) {
        return new FilesFrame(context);
    }

    public final JFrame ui() {
        return frame;
    }
}
