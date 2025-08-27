package de.team33.sphinx.gamma.table;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public abstract class GenericModel<E> extends AbstractTableModel {

    private final List<? extends Column<E, ?>> columns;

    protected GenericModel(final List<? extends Column<E, ?>> columns) {
        this.columns = columns;
    }

    protected abstract List<E> list();

    @Override
    public final int getRowCount() {
        return list().size();
    }

    @Override
    public final int getColumnCount() {
        return columns.size();
    }

    @Override
    public final String getColumnName(final int columnIndex) {
        return columns.get(columnIndex).title();
    }

    @Override
    public final Class<?> getColumnClass(final int columnIndex) {
        return columns.get(columnIndex).type();
    }

    @Override
    public final boolean isCellEditable(final int rowIndex, final int columnIndex) {
        // TODO? preliminary ...
        return super.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public final Object getValueAt(final int rowIndex, final int columnIndex) {
        return columns.get(columnIndex).map(list().get(rowIndex));
    }

    @Override
    public final void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        // TODO? preliminary ...
        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    public interface Column<E, C extends Comparable<C>> {

        String title();

        Class<C> type();

        C map(E element);
    }
}
