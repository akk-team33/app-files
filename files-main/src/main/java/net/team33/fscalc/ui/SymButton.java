package net.team33.fscalc.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class SymButton extends JToggleButton implements ActionListener {
    public SymButton(Icon icon, String toolTip) {
        super(icon);
        this.setMargin(new Insets(0, 0, 0, 0));
        this.setToolTipText(toolTip);
        this.addActionListener(this);
    }

    @Override
    public final void actionPerformed(ActionEvent e) {
        this.setSelected(!this.isSelected());
    }
}
