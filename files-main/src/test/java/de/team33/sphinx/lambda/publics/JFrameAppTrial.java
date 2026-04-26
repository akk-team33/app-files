package de.team33.sphinx.lambda.publics;

import de.team33.sphinx.lambda.JFrameApp;
import de.team33.sphinx.metis.JFrames;

import javax.swing.*;

class JFrameAppTrial extends JFrameApp {

    public static void main(final String[] args) {
        launch(new JFrameAppTrial());
    }

    @Override
    protected final JFrame newFrame() {
        return JFrames.builder(getClass().getCanonicalName())
                      .setContentPane(new JLabel("Hello " + getClass().getCanonicalName()))
                      .build();
    }
}