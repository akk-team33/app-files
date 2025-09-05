package de.team33.sphinx.gamma.table;

import javax.swing.*;
import java.util.List;

/**
 * @param <C> A type that represents a column descriptor.
 */
public abstract class HeadRenderer<C extends RowModel.Column<?>> extends BaseRenderer<String, C> {

    /**
     * @param columns An <b>immutable</b> {@link List} of column descriptors.
     */
    protected HeadRenderer(final List<? extends C> columns) {
        super(new JTable().getTableHeader().getDefaultRenderer(), columns, String.class);
    }

    @Override
    protected final void setup(final JLabel result, final String value, final C column) {
        // nothing to do
    }
}
