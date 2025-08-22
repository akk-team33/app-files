package de.team33.files.ui;

import de.team33.files.uix.SwingTrial;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.alpha.visual.JSplitPanes;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.function.UnaryOperator;

final class FileTreeTrial extends SwingTrial {

    private static final UnaryOperator<Path> NORMAL_PATH = path -> path.toAbsolutePath().normalize();

    private final Variable<Path> cwd = new Component<>(NORMAL_PATH, Path.of("."));

    public static void main(final String[] args) {
        run(new FileTreeTrial());
    }

    @Override
    protected Container contentPane() {
        return JSplitPanes.builder()
                          .setLeftComponent(FileTree.serving(cwd).component())
                          .setRightComponent(FileTree.serving(cwd).component())
                          .build();
    }

    @Override
    protected void setupFrame(final JFrame jFrame) {
        cwd.retrieve(path -> jFrame.setTitle(path.toString()));
    }
}