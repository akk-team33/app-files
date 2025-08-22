package de.team33.files.ui;

import de.team33.files.uix.SwingTrial;
import de.team33.sphinx.alpha.visual.JSplitPanes;

import javax.swing.*;
import java.awt.*;

final class FileTableTrial extends SwingTrial {

    private final Context context = new Context();

    public static void main(final String[] args) {
        run(new FileTableTrial());
    }

    @Override
    protected Container contentPane() {
        return JSplitPanes.builder()
                          .setLeftComponent(FileTree.by(context).component())
                          .setRightComponent(FileTable.by(context).component())
                          .build();
    }

    @Override
    protected void setupFrame(final JFrame jFrame) {
        context.cwd().retrieve(path -> jFrame.setTitle(path.toString()));
    }
}