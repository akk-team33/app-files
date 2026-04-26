package de.team33.files.luna;

import de.team33.files.luna.busyness.Backend;
import de.team33.files.luna.ui.FilesFrame;
import de.team33.files.luna.ui.FilesIcons;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.lambda.JFrameApp;
import net.team33.fscalc.ui.rsrc.Ico;

import javax.swing.*;
import java.nio.file.Path;
import java.util.concurrent.Executor;

public class Files extends JFrameApp {

    private final Icons icons = new Icons();
    private final Backend backend = new Backend();
    private final FilesFrame.Context context = new Context();

    public static void main(final String[] args) {
        start(new Files());
    }

    @Override
    protected final JFrame newFrame() {
        return FilesFrame.by(context).ui();
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    private static class Icons implements FilesIcons {

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

        @Override
        public final Executor executor() {
            return backend.executor();
        }

        @Override
        public final Variable<Path> cwd() {
            return backend.cwd();
        }

        @Override
        public final Icons icons() {
            return icons;
        }
    }
}