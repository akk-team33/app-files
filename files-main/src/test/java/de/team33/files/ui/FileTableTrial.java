package de.team33.files.ui;

import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.alpha.visual.JSplitPanes;

import javax.swing.*;
import java.nio.file.Path;
import java.util.function.UnaryOperator;

final class FileTableTrial extends SwingTrial {

    private static final UnaryOperator<Path> NORMAL_PATH = path -> path.toAbsolutePath().normalize();

    private FileTableTrial() {
        this(new Component<>(NORMAL_PATH, Path.of(".")));
    }

    private FileTableTrial(final Variable<Path> path) {
        super(trialPane(path));
        path.retrieve(this::setPath);
    }

    private static JSplitPane trialPane(final Variable<Path> path) {
        return JSplitPanes.builder()
                          .setLeftComponent(FileTree.serving(path).component())
                          .setRightComponent(FileTable.serving(path).component())
                          .build();
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new FileTableTrial());
    }

    private void setPath(final Path path) {
        setTitle(path.toAbsolutePath().normalize().toString());
    }
}