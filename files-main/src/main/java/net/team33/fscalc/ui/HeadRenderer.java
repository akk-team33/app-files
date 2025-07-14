//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.fscalc.ui;

import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.info.FileService;
import net.team33.fscalc.work.Context;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;

public abstract class HeadRenderer extends JPanel implements TableCellRenderer {
    private TITLE title = new TITLE();
    private CellRenderer info = new INFO();

    protected abstract Context getContext();

    public HeadRenderer() {
        super(new GridLayout(0, 1, 1, 1));
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(0, 2, 0, 2)));
        this.add(this.title);
        this.add(this.info);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        column = table.convertColumnIndexToModel(column);
        this.title.setText(value.toString(), column);
        File path = this.getContext().getPath();
        FileInfo fi = FileService.getInstance().getInfo(path);
        this.info.setFileInfo(fi, column);
        return this;
    }

    private class INFO extends CellRenderer {
        INFO() {
            super(true);
        }
    }

    private class TITLE extends JLabel {
        TITLE() {
        }

        public void setText(String text, int column) {
            if (column > 0) {
                this.setHorizontalAlignment(4);
            } else {
                this.setHorizontalAlignment(2);
            }

            this.setText(text);
        }
    }
}
