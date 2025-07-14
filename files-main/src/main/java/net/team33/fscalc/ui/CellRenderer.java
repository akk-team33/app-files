package net.team33.fscalc.ui;

import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.ui.rsrc.Ico;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CellRenderer extends JLabel implements TableCellRenderer {
    public CellRenderer(final boolean forHead) {
        if (forHead) {
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        } else {
            setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 3));
        }

        setFont(new Font(getFont().getName(), 0, getFont().getSize()));
    }

    public CellRenderer() {
        this(false);
    }

    @Override
    public final Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        setForeground(table.getSelectionForeground());
        setBackground(table.getSelectionBackground());
        setOpaque(isSelected);
        setFileInfo((FileInfo) value, table.convertColumnIndexToModel(column));
        return this;
    }

    public final void setFileInfo(final FileInfo value, final int column) {
        if (column > 0) {
            setHorizontalAlignment(4);
        } else {
            setHorizontalAlignment(2);
        }

        setIcon(getIcon(value, column));
        setText(getText(value, column));
    }

    private String getText(final FileInfo fileInfo, final int column) {
        return column == 0 ? getName(fileInfo) : getValue(fileInfo, column);
    }

    private Icon getIcon(final FileInfo fileInfo, final int column) {
        if (column > 0) {
            return null;
        } else if (fileInfo.getPath().isFile()) {
            return Ico.FILE;
        } else if (!fileInfo.getPath().isDirectory()) {
            return null;
        } else if (!fileInfo.isDefinite()) {
            return Ico.CLSDIRQ;
        } else {
            return fileInfo.getErrorCount() > 0L ? Ico.CLSDIRX : Ico.CLSDIR;
        }
    }

    private String getValue(final FileInfo it, int col) {
        if (!it.isDefinite()) {
            col = -1;
        }

        final String apendix = it.getErrorCount() > 0L ? "*" : "";
        switch (col) {
            case 1:
                return String.format("%,d kB%s", (it.getTotalSize() + 1023L) / 1024L, apendix);
            case 2:
                return String.format("%,d kB%s", (it.getAverageSize() + 1023L) / 1024L, apendix);
            case 3:
                return String.format("%,d%s", it.getFileCount(), apendix);
            case 4:
                return String.format("%,d%s", it.getDirCount(), apendix);
            case 5:
                return String.format("%,d%s", it.getErrorCount(), apendix);
            default:
                return "?";
        }
    }

    private String getName(final FileInfo it) {
        return it.getPath().getParentFile() == null ? it.getPath().getAbsolutePath() : it.getPath().getName();
    }
}
