package de.team33.files.ui;

import de.team33.sphinx.lambda.SwingApp;
import de.team33.sphinx.metis.JFrames;
import de.team33.sphinx.metis.JSplitPanes;

import javax.swing.*;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

final class FileTableTrial extends SwingApp {

    private final Context context = new Context();

    public static void main(final String[] args) {
        start(new FileTableTrial());
    }

    @Override
    protected JFrame newFrame() {
        return JFrames.builder(getClass().getCanonicalName())
                      .setContentPane(JSplitPanes.builder()
                                                 .setLeftComponent(FileTree.by(context).component())
                                                 .setRightComponent(FileTable.by(context).component())
                                                 .build())
                      .setup(jFrame -> context.cwd().subscribe(INIT, path -> jFrame.setTitle(path.toString())))
                      .build();
    }
}