package de.team33.sphinx.gamma.table;

import de.team33.sphinx.metis.JLabels;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Basic {@link TableCellRenderer} implementation that assumes that the value of a table cell is represented
 * by a certain type and is rendered by a JLabel.
 *
 * @param <V> The type of values to be rendered.
 * @param <C> A type that represents a {@linkplain Column column descriptor}.
 */
public abstract class CellRenderer<V, C extends CellRenderer.Column> implements TableCellRenderer {

    private final TableCellRenderer backing;
    private final Class<? extends V> valueClass;

    protected CellRenderer(final Class<? extends V> valueClass) {
        this(new JTable().getDefaultRenderer(valueClass), valueClass);
    }

    CellRenderer(final TableCellRenderer backing,
                 final Class<? extends V> valueClass) {
        this.backing = backing;
        this.valueClass = valueClass;
    }

    protected abstract List<? extends C> columns();

    @Override
    public final Component getTableCellRendererComponent(final JTable table,
                                                         final Object value,
                                                         final boolean isSelected,
                                                         final boolean hasFocus,
                                                         final int rowIndex,
                                                         final int colIndex) {
        final Component result =
                backing.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, colIndex);
        return charged((JLabel) result,
                       valueClass.cast(value),
                       columns().get(table.convertColumnIndexToModel(colIndex)));
    }

    private JLabel charged(final JLabel result, final V value, final C column) {
        return JLabels.charger(result)
                      .setup(label -> setup(label, value, column))
                      .setHorizontalAlignment(column.horizontalAlignment())
                      .charged();
    }

    protected abstract void setup(JLabel result, final V value, final C column);

    /**
     * Represents a table column description to be used in the context of a {@link CellRenderer}.
     */
    public interface Column {

        /**
         * Returns the horizontal alignment to be applied in <em>this</em> column.
         */
        int horizontalAlignment();
    }
}
