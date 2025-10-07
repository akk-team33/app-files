package de.team33.files.luna;

import de.team33.files.luna.ui.FilesFrame;
import de.team33.sphinx.lambda.SwingApp;

import javax.swing.*;

public class Files extends SwingApp {

    private Context context = new Context();

    public static void main(final String[] args) {
        start(new Files());
    }

    @Override
    protected final JFrame newFrame() {
        return FilesFrame.by(context).ui();
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    private static class Context implements FilesFrame.Context {
    }
}