package de.team33.files.ui;

import de.team33.files.testing.SwingTrial;
import de.team33.sphinx.metis.JSplitPanes;

import javax.swing.*;
import java.awt.*;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

final class FileTreeTrial extends SwingTrial {

    private final FileTree.Context context = new Context();

    public static void main(final String[] args) {
        run(new FileTreeTrial());
    }

    @Override
    protected Container contentPane() {
        return JSplitPanes.builder()
                          .setLeftComponent(FileTree.by(context).component())
                          .setRightComponent(FileTree.by(context).component())
                          .build();
    }

    @Override
    protected void setupFrame(final JFrame jFrame) {
        context.cwd().subscribe(INIT, path -> jFrame.setTitle(path.toString()));
    }
}