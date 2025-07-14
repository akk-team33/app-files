package net.team33.fscalc.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class SymButton extends JToggleButton implements ActionListener {
    public SymButton(final Icon icon, final String toolTip) {
        super(icon);
        setMargin(new Insets(0, 0, 0, 0));
        setToolTipText(toolTip);
        addActionListener(this);
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        setSelected(!isSelected());
    }
}
