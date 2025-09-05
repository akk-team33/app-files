package de.team33.sphinx.gamma.table;

import de.team33.sphinx.metis.JLabels;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * @param <V> The type of values to be rendered.
 * @param <C> A type that represents a column descriptor.
 */
abstract class BaseRenderer<V, C extends RowModel.Column<?>> implements TableCellRenderer {

    private final TableCellRenderer backing;
    private final List<? extends C> columns;
    private final Class<? extends V> valueClass;

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    BaseRenderer(final TableCellRenderer backing, final List<? extends C> columns, final Class<? extends V> valueClass) {
        this.backing = backing;
        this.columns = columns;
        this.valueClass = valueClass;
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    protected final List<? extends C> columns() {
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
        return charged((JLabel) result, valueClass.cast(value), columns.get(table.convertColumnIndexToModel(colIndex)));
    }

    private JLabel charged(final JLabel result, final V value, final C column) {
        return JLabels.charger(result)
                      .setup(label -> setup(label, value, column))
                      .setHorizontalAlignment(column.horizontalAlignment())
                      .charged();
    }

    protected abstract void setup(JLabel result, final V value, final C column);
}
