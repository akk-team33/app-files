package de.team33.files.luna;

import de.team33.files.luna.busyness.Backend;
import de.team33.files.luna.context.UIContext;
import de.team33.files.luna.ui.FilesFrame;
import de.team33.sphinx.lambda.JFrameApp;

import javax.swing.*;

public class Files extends JFrameApp {

    private final UIContext context = new Backend();

    public static void main(final String[] args) {
        launch(new Files());
    }

    @Override
    protected final JFrame newFrame() {
        return FilesFrame.by(context).ui();
    }
}