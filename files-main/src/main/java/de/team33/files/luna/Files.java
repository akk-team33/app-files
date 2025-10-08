package de.team33.files.luna;

import de.team33.files.luna.busyness.Backend;
import de.team33.files.luna.ui.FilesFrame;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.lambda.SwingApp;

import javax.swing.*;
import java.nio.file.Path;

public class Files extends SwingApp {

    private final Backend backend = new Backend();
    private final Context context = new Context();

    public static void main(final String[] args) {
        start(new Files());
    }

    @Override
    protected final JFrame newFrame() {
        return FilesFrame.by(context).ui();
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    private class Context implements FilesFrame.Context {

        @Override
        public Variable<Path> cwd() {
            return backend.cwd();
        }
    }
}