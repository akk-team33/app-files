package net.team33.fscalc.ui;

import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.info.FileService;
import net.team33.fscalc.work.Context;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;

public abstract class HeadRenderer extends JPanel implements TableCellRenderer {
    private final TITLE title = new TITLE();
    private final CellRenderer info = new INFO();

    protected abstract Context getContext();

    public HeadRenderer() {
        super(new GridLayout(0, 1, 1, 1));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(0, 2, 0, 2)));
        add(title);
        add(info);
    }

    @Override
    public final Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, int column) {
        column = table.convertColumnIndexToModel(column);
        title.setText(value.toString(), column);
        final File path = getContext().getPath();
        final FileInfo fi = FileService.getInstance().getInfo(path);
        info.setFileInfo(fi, column);
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

        public final void setText(final String text, final int column) {
            if (column > 0) {
                setHorizontalAlignment(4);
            } else {
                setHorizontalAlignment(2);
            }

            setText(text);
        }
    }
}
