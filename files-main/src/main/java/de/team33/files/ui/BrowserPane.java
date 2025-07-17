package de.team33.files.ui;

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

    private BrowserPane(final Context context) {
        super(new GridBagLayout());
        final InfoTable table = infoTable(context);
        add(new JScrollPane(infoTable(context)), GBC_TABLE);
        add(new JPanel(), GBC_SPACE1);
        add(ControlPane.by(table, context), GBC_CONTROLS);
        add(new JPanel(), GBC_SPACE2);
    }

    public static BrowserPane by(final Context context) {
        return new BrowserPane(context);
    }

    private static InfoTable infoTable(final Context context) {
        return InfoTable.by(context);
    }
}
