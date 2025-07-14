//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.fscalc.ui;

import net.team33.fscalc.ui.model.InfoTableModel;
import net.team33.fscalc.work.Context;

import javax.swing.*;
import java.awt.*;

public abstract class BrowserPane extends JPanel {
    private static final Insets GBC_INSETS = new Insets(0, 0, 0, 0);
    private static final GridBagConstraints GBC_TABLE;
    private static final GridBagConstraints GBC_CONTROLS;
    private static final GridBagConstraints GBC_SPACE1;
    private static final GridBagConstraints GBC_SPACE2;

    static {
        GBC_TABLE = new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, 10, 1, GBC_INSETS, 0, 0);
        GBC_CONTROLS = new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, 10, 2, GBC_INSETS, 0, 0);
        GBC_SPACE1 = new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0, 10, 2, GBC_INSETS, 0, 0);
        GBC_SPACE2 = new GridBagConstraints(2, 1, 1, 1, 0.5, 0.0, 10, 2, GBC_INSETS, 0, 0);
    }

    protected abstract Context getContext();

    public BrowserPane() {
        super(new GridBagLayout());
        InfoTable table = new TABLE();
        ControlPane controls = new CONTROL(table);
        this.add(new JScrollPane(table), GBC_TABLE);
        this.add(new JPanel(), GBC_SPACE1);
        this.add(controls, GBC_CONTROLS);
        this.add(new JPanel(), GBC_SPACE2);
    }

    private class CONTROL extends ControlPane {
        public CONTROL(InfoTable table) {
            super(table);
        }

        protected Context getContext() {
            return BrowserPane.this.getContext();
        }
    }

    private class TABLE extends InfoTable {
        private TABLE() {
            super(new InfoTableModel(BrowserPane.this.getContext()));
        }

        protected Context getContext() {
            return BrowserPane.this.getContext();
        }
    }
}
