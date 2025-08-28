package de.team33.sphinx.gamma.table;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @param <R> The type that represents the data of each row as a whole.
 */
public abstract class GenericModel<R> extends AbstractTableModel {

    protected abstract List<? extends R> rows();

    protected abstract List<? extends Column<? super R, ?>> columns();

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
        return columns().get(columnIndex)
                        .map(rows().get(rowIndex));
    }

    @Override
    public final void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        // TODO? preliminary ...
        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    /**
     * @param <R> The type that represents the data of each row as a whole.
     * @param <C> The type that represents the data of the column in question.
     */
    public interface Column<R, C extends Comparable<C>> {

        String title();

        Class<C> type();

        C map(R row);
    }
}
