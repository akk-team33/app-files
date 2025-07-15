package net.team33.fscalc.ui;

import net.team33.fscalc.ui.model.InfoTableModel;
import net.team33.fscalc.work.Context;

import javax.swing.*;
import java.awt.*;

public class BrowserPane extends JPanel {

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

    public BrowserPane(final Context context) {
        super(new GridBagLayout());
        final InfoTable table = table(context);
        final ControlPane controls = control(table, context);
        add(new JScrollPane(table), GBC_TABLE);
        add(new JPanel(), GBC_SPACE1);
        add(controls, GBC_CONTROLS);
        add(new JPanel(), GBC_SPACE2);
    }

    private static ControlPane control(final InfoTable table, final Context context) {
        return new ControlPane(table, context);
    }

    private static InfoTable table(final Context context) {
        return new InfoTable(new InfoTableModel(context), context);
    }
}
