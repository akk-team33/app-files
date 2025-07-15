package net.team33.fscalc.ui;

import net.team33.fscalc.work.Context;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.function.Consumer;

public class ControlPane extends JPanel {

    public ControlPane(final InfoTable table, final Context context) {
        super(new GridLayout(1, 0, 2, 2));
        table.getRegister().add(new ITBL_LSTNR());
        final ACTNBUTTON delButton = new DELBUTTON(table, context);
        add(delButton);
    }

    private abstract class ACTNBUTTON extends JButton implements Consumer<InfoTable.UpdateSelection>, ActionListener {
        protected ACTNBUTTON(final String text, final InfoTable table) {
            super(text);
            addActionListener(this);
            table.getRegister().add(this);
        }

        @Override
        public final void accept(final InfoTable.UpdateSelection message) {
            setEnabled(message.getSender().getSelectedRowCount() > 0);
        }
    }

    public class DELBUTTON extends ACTNBUTTON {
        private final InfoTable table;
        private final Context context;

        protected DELBUTTON(final InfoTable table, final Context context) {
            super("Löschen", table);
            this.table = table;
            this.context = context;
        }

        @Override
        public final void actionPerformed(final ActionEvent e) {
            final int[] rows = table.getSelectedRows();
            final File[] paths = new File[rows.length];

            for(int i = 0; i < rows.length; ++i) {
                paths[i] = table.getModel().getValueAt(rows[i], 0).getPath();
            }
            context.startDeletion(paths);
        }
    }

    private class ITBL_LSTNR implements Consumer<InfoTable.UpdateSelection> {

        @Override
        public void accept(final InfoTable.UpdateSelection message) {
        }
    }
}
