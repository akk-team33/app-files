package de.team33.sphinx.gamma.table;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.List;

/**
 * A basic {@link TableModel} for {@linkplain JTable tables} that essentially display a {@link List} of elements
 * of a specific type, with each row of the table representing one of these elements.
 * The table's columns are then determined by the individual properties of the elements.
 *
 * @param <E> The type of list elements, each of which is represented as a table row.
 */
public abstract class RowModel<E> extends AbstractTableModel {

    /**
     * Returns the currently valid {@link List} of elements that should be displayed as one table row each.
     */
    protected abstract List<? extends E> rows();

    /**
     * Returns a {@link List} of {@link Column}s describing the columns of the table in question.
     */
    protected abstract List<? extends Column<E>> columns();

    @Override
    public final int getRowCount() {
        return rows().size();
    }

    @Override
    public final int getColumnCount() {
        return columns().size();
    }

    @Override
    public final String getColumnName(final int columnIndex) {
        return columns().get(columnIndex).title();
    }

    @Override
    public final Class<?> getColumnClass(final int columnIndex) {
        return columns().get(columnIndex).type();
    }

    @Override
    public final boolean isCellEditable(final int rowIndex, final int columnIndex) {
        // TODO? preliminary ...
        return super.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public final Object getValueAt(final int rowIndex, final int columnIndex) {
        final E row = rows().get(rowIndex);
        return columns().get(columnIndex).map(row);
    }

    @Override
    public final void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        // TODO? preliminary ...
        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    /**
     * Represents a table column description to be used in the context of a {@link RowModel}.
     *
     * @param <E> The type of list elements, each of which is represented as a table row.
     */
    public interface Column<E> {

        /**
         * Returns the title of <em>this</em> column.
         */
        String title();

        /**
         * Returns the horizontal alignment to be applied in <em>this</em> column.
         */
        int horizontalAlignment();

        /**
         * Returns the {@link Class} that represents the type of property to be displayed in <em>this</em> column.
         * <p>
         * A result of {@link #map(Object)} must match that type.
         */
        Class<?> type();

        /**
         * Returns the property value belonging to <em>this</em> column from an element.
         * <p>
         * A result must match {@link #type()}.
         */
        Object map(E element);
    }
}
