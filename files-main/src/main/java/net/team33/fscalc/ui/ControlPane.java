package net.team33.fscalc.ui;

import net.team33.fscalc.work.Context;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.function.Consumer;

public abstract class ControlPane extends JPanel {
    protected abstract Context getContext();

    public ControlPane(final InfoTable table) {
        super(new GridLayout(1, 0, 2, 2));
        table.getRegister().add(new ITBL_LSTNR());
        final ACTNBUTTON delButton = new DELBUTTON(table);
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

        protected DELBUTTON(final InfoTable table) {
            super("Löschen", table);
            this.table = table;
        }

        @Override
        public final void actionPerformed(final ActionEvent e) {
            final int[] rows = table.getSelectedRows();
            final File[] paths = new File[rows.length];

            for(int i = 0; i < rows.length; ++i) {
                paths[i] = table.getModel().getValueAt(rows[i], 0).getPath();
            }

            getContext().startDeletion(paths);
        }
    }

    private class ITBL_LSTNR implements Consumer<InfoTable.UpdateSelection> {

        @Override
        public void accept(final InfoTable.UpdateSelection message) {
        }
    }
}
