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

    public ControlPane(InfoTable table) {
        super(new GridLayout(1, 0, 2, 2));
        table.getRegister().add(new ITBL_LSTNR());
        ACTNBUTTON delButton = new DELBUTTON(table);
        this.add(delButton);
    }

    private abstract class ACTNBUTTON extends JButton implements Consumer<InfoTable.UpdateSelection>, ActionListener {
        protected ACTNBUTTON(String text, InfoTable table) {
            super(text);
            this.addActionListener(this);
            table.getRegister().add(this);
        }

        @Override
        public final void accept(InfoTable.UpdateSelection message) {
            this.setEnabled(((InfoTable)message.getSender()).getSelectedRowCount() > 0);
        }
    }

    public class DELBUTTON extends ACTNBUTTON {
        private final InfoTable table;

        protected DELBUTTON(InfoTable table) {
            super("Löschen", table);
            this.table = table;
        }

        @Override
        public final void actionPerformed(ActionEvent e) {
            int[] rows = this.table.getSelectedRows();
            File[] paths = new File[rows.length];

            for(int i = 0; i < rows.length; ++i) {
                paths[i] = this.table.getModel().getValueAt(rows[i], 0).getPath();
            }

            ControlPane.this.getContext().startDeletion(paths);
        }
    }

    private class ITBL_LSTNR implements Consumer<InfoTable.UpdateSelection> {

        @Override
        public void accept(InfoTable.UpdateSelection message) {
        }
    }
}
