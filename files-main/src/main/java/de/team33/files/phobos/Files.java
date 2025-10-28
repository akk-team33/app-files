package de.team33.files.phobos;

import de.team33.files.phobos.ui.Frame;
import de.team33.sphinx.lambda.SwingApp;

import javax.swing.*;

public class Files extends SwingApp {

    private final Frame.Context context = new Context();

    public static void main(final String[] args) {
        start(new Files());
    }

    @Override
    protected final JFrame newFrame() {
        return Frame.by(context).ui();
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    private class Context implements Frame.Context {
    }
}