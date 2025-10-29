package de.team33.files.phobos.ui;

import de.team33.sphinx.metis.JComponents;
import de.team33.sphinx.metis.JPanels;

import javax.swing.*;
import java.awt.*;

final class Template {

    private final JComponent component;

    private Template(final Context context) {
        component = JComponents.builder(null)
                               .build();
    }

    static Template by(final Context context) {
        return new Template(context);
    }

    public final JComponent ui() {
        return component;
    }

    public interface Context {

    }

    private final class SubPanel {

        private final JPanel panel;

        private SubPanel() {
            panel = JPanels.builder(new GridLayout(1, 4))
                           .build();
        }

        public final JPanel ui() {
            return panel;
        }
    }
}
