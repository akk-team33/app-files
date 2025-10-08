package de.team33.files.luna;

import de.team33.files.luna.busyness.Backend;
import de.team33.files.luna.ui.FilesFrame;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.lambda.SwingApp;
import net.team33.fscalc.ui.rsrc.Ico;

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

    private static class Icons implements FilesFrame.Icons {

        @Override
        public final Icon stdFolder() {
            return Ico.CLSDIR;
        }

        @Override
        public final Icon opnFolder() {
            return Ico.OPNDIR;
        }

        @Override
        public final Icon stdFile() {
            return Ico.FILE;
        }
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    private class Context implements FilesFrame.Context {

        private final Icons icons = new Icons();

        @Override
        public Variable<Path> cwd() {
            return backend.cwd();
        }

        @Override
        public Icons icons() {
            return this.icons;
        }
    }
}