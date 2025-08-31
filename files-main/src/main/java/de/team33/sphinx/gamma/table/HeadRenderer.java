package de.team33.sphinx.gamma.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * @param <C> A type that represents a column descriptor.
 */
public abstract class HeadRenderer<C> implements TableCellRenderer {

    private final TableCellRenderer backing = new JTable().getTableHeader().getDefaultRenderer();
    private final List<? extends C> columns;

    /**
     * @param columns An <b>immutable</b> {@link List} of column descriptors.
     */
    public HeadRenderer(final List<? extends C> columns) {
        //noinspection AssignmentOrReturnOfFieldWithMutableType
        this.columns = columns;
    }

    public final List<? extends C> columns() {
        //noinspection AssignmentOrReturnOfFieldWithMutableType
        return columns;
    }

    @Override
    public final Component getTableCellRendererComponent(final JTable table,
                                                         final Object value,
                                                         final boolean isSelected,
                                                         final boolean hasFocus,
                                                         final int rowIndex,
                                                         final int colIndex) {
        final Component result =
                backing.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, colIndex);
        return charged((JLabel) result, columns.get(table.convertColumnIndexToModel(colIndex)));
    }

    protected abstract JLabel charged(final JLabel result, final C column);
}
