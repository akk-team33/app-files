package de.team33.files.ui;

import de.team33.patterns.serving.alpha.Component;

import javax.swing.*;
import java.nio.file.Path;
import java.util.function.UnaryOperator;

final class FileTreePaneTrial extends SwingTrial {

    private static final UnaryOperator<Path> NORMAL_PATH = path -> path.toAbsolutePath().normalize();

    private FileTreePaneTrial() {
        super(FileTreePane.using(new Component<>(NORMAL_PATH, Path.of("."))));
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new FileTreePaneTrial());
    }
}