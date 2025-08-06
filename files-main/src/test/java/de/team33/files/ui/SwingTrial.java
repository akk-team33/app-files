package de.team33.files.ui;

import de.team33.sphinx.alpha.visual.JFrames;

import javax.swing.*;
import java.awt.*;

class SwingTrial extends JFrame implements Runnable {

    private final Container contentPane;

    SwingTrial(final Container contentPane) {
        this.contentPane = contentPane;
        setTitle(null);
    }

    @Override
    public final void setTitle(final String title) {
        @SuppressWarnings("VariableNotUsedInsideIf")
        final String format = (null == title) ? "%2$s" : "%1$s - %2$s";
        super.setTitle(format.formatted(title, getClass().getSimpleName()));
    }

    @Override
    public final void run() {
        JFrames.charger(this)
               .setContentPane(contentPane)
               .setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
               .setLocationByPlatform(true)
               .pack()
               .setVisible(true);
    }
}
