package de.team33.sphinx.lambda.publics;

import de.team33.sphinx.lambda.SwingApp;
import de.team33.sphinx.metis.JFrames;

import javax.swing.*;

class SwingAppTrial extends SwingApp {

    public static void main(final String[] args) {
        start(new SwingAppTrial());
    }

    @Override
    protected final JFrame newFrame() {
        return JFrames.builder(getClass().getCanonicalName())
                      .setContentPane(new JLabel("Hello " + getClass().getCanonicalName()))
                      .build();
    }
}