package de.team33.files.ui;

import de.team33.files.uix.SwingTrial;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.alpha.visual.JSplitPanes;
import net.team33.fscalc.ui.rsrc.Ico;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.function.UnaryOperator;

final class FileTableTrial extends SwingTrial {

    private static final UnaryOperator<Path> NORMAL_PATH = path -> path.toAbsolutePath().normalize();

    private final Context context = new Context();

    public static void main(final String[] args) {
        run(new FileTableTrial());
    }

    @Override
    protected Container contentPane() {
        return JSplitPanes.builder()
                          .setLeftComponent(FileTree.serving(context.cwd()).component())
                          .setRightComponent(FileTable.by(context).component())
                          .build();
    }

    @Override
    protected void setupFrame(final JFrame jFrame) {
        context.cwd().retrieve(path -> jFrame.setTitle(path.toString()));
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    private static class Icons implements FileTable.Icons {

        @Override
        public final Icon stdFolder() {
            return Ico.CLSDIR;
        }

        @Override
        public final Icon stdFile() {
            return Ico.FILE;
        }
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    private static class Context implements FileTable.Context {

        private static final Icons ICONS = new Icons();

        private final Variable<Path> cwd = new Component<>(NORMAL_PATH, Path.of("."));

        @Override
        public final Icons icons() {
            return ICONS;
        }

        @Override
        public final List<FileTable.Column> columns() {
            return List.of(FileTable.Column.values());
        }

        @Override
        public final Variable<Path> cwd() {
            return cwd;
        }
    }
}