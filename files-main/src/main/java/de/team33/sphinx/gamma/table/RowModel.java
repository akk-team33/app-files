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
 * A {@link TableModel} that is based on the fact that all table rows are represented by
 * a specific type whose properties form the table columns.
 *
 * @param <E> The type of list elements.
 */
public abstract class RowModel<E> extends AbstractTableModel {

    protected abstract List<? extends E> rows();

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
}
