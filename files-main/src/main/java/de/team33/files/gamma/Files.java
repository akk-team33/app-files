package de.team33.files.gamma;

import de.team33.files.gamma.ui.FilesFrame;
import de.team33.sphinx.lambda.SwingApp;

import javax.swing.*;

public class Files extends SwingApp {

    private Context context = new Context();

    public static void main(final String[] args) {
        start(new Files());
    }

    @Override
    protected final JFrame newFrame() {
        return FilesFrame.with(context).ui();
    }

}
