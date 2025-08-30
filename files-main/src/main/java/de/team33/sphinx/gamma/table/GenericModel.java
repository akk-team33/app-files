package de.team33.sphinx.gamma.table;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.List;

/**
 * A {@link TableModel} that is based on the fact that all table rows are represented by
 * a specific type whose properties form the table columns.
 *
 * @param <R> The type that represents the table rows.
 */
public abstract class GenericModel<R, C> extends AbstractTableModel {

    protected abstract List<? extends R> rows();

    protected abstract List<? extends Column<R, ?>> columns();

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
        final R row = rows().get(rowIndex);
        return columns().get(columnIndex).map(row);
    }

    @Override
    public final void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        // TODO? preliminary ...
        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    /**
     * @param <R> The type that represents the data of each row as a whole.
     * @param <P> The type that represents the cell property of the column in question.
     */
    public interface Column<R, P> {

        String title();

        Class<P> type();

        P map(R row);
    }
}
