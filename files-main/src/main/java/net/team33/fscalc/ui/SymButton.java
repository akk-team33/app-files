package net.team33.fscalc.ui;

import de.team33.sphinx.alpha.visual.JButtons;

import javax.swing.*;
import java.awt.*;

final class SymButton {

    private static final Insets INSETS = new Insets(0, 0, 0, 0);

    private SymButton() {
    }

    static JButtons.Builder<JButton> builder(final Icon icon, final String toolTip) {
        return JButtons.builder()
                       .setMargin(INSETS)
                       .setIcon(icon)
                       .setToolTipText(toolTip);
    }
}
