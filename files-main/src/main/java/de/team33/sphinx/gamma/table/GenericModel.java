package de.team33.sphinx.gamma.table;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.function.Function;

/**
 * @param <R> The type that represents the data of each row as a whole.
 * @param <C> The type of context information used for {@linkplain Column#mapping(Object) column mapping}
 */
public abstract class GenericModel<R, C> extends AbstractTableModel {

    protected abstract C context();

    protected abstract List<? extends R> rows();

    protected abstract List<? extends Column<R, C, ?>> columns();

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
        return columns().get(columnIndex)
                        .mapping(context())
                        .apply(row);
    }

    @Override
    public final void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        // TODO? preliminary ...
        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    /**
     * @param <R> The type that represents the data of each row as a whole.
     * @param <C> The type of context information used for {@linkplain #mapping(Object) mapping}
     * @param <P> The type that represents the cell property of the column in question.
     */
    public interface Column<R, C, P> {

        String title();

        Class<P> type();

        Function<R, P> mapping(C context);
    }
}
