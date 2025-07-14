package net.team33.fscalc.ui;

import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.ui.rsrc.Ico;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CellRenderer extends JLabel implements TableCellRenderer {
    public CellRenderer(boolean forHead) {
        if (forHead) {
            this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        } else {
            this.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 3));
        }

        this.setFont(new Font(this.getFont().getName(), 0, this.getFont().getSize()));
    }

    public CellRenderer() {
        this(false);
    }

    @Override
    public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setForeground(table.getSelectionForeground());
        this.setBackground(table.getSelectionBackground());
        this.setOpaque(isSelected);
        this.setFileInfo((FileInfo)value, table.convertColumnIndexToModel(column));
        return this;
    }

    public final void setFileInfo(FileInfo value, int column) {
        if (column > 0) {
            this.setHorizontalAlignment(4);
        } else {
            this.setHorizontalAlignment(2);
        }

        this.setIcon(this.getIcon(value, column));
        this.setText(this.getText(value, column));
    }

    private String getText(FileInfo fileInfo, int column) {
        return column == 0 ? this.getName(fileInfo) : this.getValue(fileInfo, column);
    }

    private Icon getIcon(FileInfo fileInfo, int column) {
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

    private String getValue(FileInfo it, int col) {
        if (!it.isDefinite()) {
            col = -1;
        }

        String apendix = it.getErrorCount() > 0L ? "*" : "";
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

    private String getName(FileInfo it) {
        return it.getPath().getParentFile() == null ? it.getPath().getAbsolutePath() : it.getPath().getName();
    }
}
