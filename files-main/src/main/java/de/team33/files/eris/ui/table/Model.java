package de.team33.files.eris.ui.table;

import de.team33.files.eris.ui.FileTable;
import de.team33.sphinx.delta.table.RowColumnModel;

import java.util.List;

public class Model extends RowColumnModel<Entry> {

    public Model(final FileTable.Context context) {
    }

    /**
     * Returns the currently valid {@link List} of elements that should be displayed as one table row each.
     */
    @Override
    protected List<? extends Entry> rows() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Returns a {@link List} of {@link Column}s describing the columns of the table in question.
     */
    @Override
    protected List<? extends Column<Entry>> columns() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
