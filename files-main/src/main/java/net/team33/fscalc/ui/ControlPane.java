//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.fscalc.ui;

import net.team33.fscalc.work.Context;
import net.team33.messaging.Listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public abstract class ControlPane extends JPanel {
    protected abstract Context getContext();

    public ControlPane(InfoTable table) {
        super(new GridLayout(1, 0, 2, 2));
        table.getRegister().add(new ITBL_LSTNR());
        ACTNBUTTON delButton = new DELBUTTON(table);
        this.add(delButton);
    }

    private abstract class ACTNBUTTON extends JButton implements Listener<InfoTable.UpdateSelection>, ActionListener {
        protected ACTNBUTTON(String text, InfoTable table) {
            super(text);
            this.addActionListener(this);
            table.getRegister().add(this);
        }

        public void pass(InfoTable.UpdateSelection message) {
            this.setEnabled(((InfoTable)message.getSender()).getSelectedRowCount() > 0);
        }
    }

    public class DELBUTTON extends ACTNBUTTON {
        private InfoTable table;

        protected DELBUTTON(InfoTable table) {
            super("Löschen", table);
            this.table = table;
        }

        public void actionPerformed(ActionEvent e) {
            int[] rows = this.table.getSelectedRows();
            File[] paths = new File[rows.length];

            for(int i = 0; i < rows.length; ++i) {
                paths[i] = this.table.getModel().getValueAt(rows[i], 0).getPath();
            }

            ControlPane.this.getContext().startDeletion(paths);
        }
    }

    private class ITBL_LSTNR implements Listener<InfoTable.UpdateSelection> {
        private ITBL_LSTNR() {
        }

        public void pass(InfoTable.UpdateSelection message) {
        }
    }
}
