package de.team33.sphinx.gamma.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * @param <V> The type of values to be rendered.
 * @param <C> A type that represents a column descriptor.
 */
public abstract class CellRenderer<V, C> implements TableCellRenderer {

    private final TableCellRenderer backing = new JTable().getDefaultRenderer(Object.class);
    private final List<? extends C> columns;
    private final Class<? extends V> valueClass;

    /**
     * @param columns    An <b>immutable</b> {@link List} of column descriptors.
     * @param valueClass The {@link Class} representation of the type of values to be rendered.
     */
    public CellRenderer(final List<? extends C> columns, final Class<? extends V> valueClass) {
        //noinspection AssignmentOrReturnOfFieldWithMutableType
        this.columns = columns;
        this.valueClass = valueClass;
    }

    public final List<? extends C> columns() {
        //noinspection AssignmentOrReturnOfFieldWithMutableType
        return columns;
    }

    public final Class<? extends V> valueClass() {
        return valueClass;
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
        return charged((JLabel) result, valueClass.cast(value), columns.get(table.convertColumnIndexToModel(colIndex)));
    }

    protected abstract JLabel charged(final JLabel result, final V value, final C column);
}
