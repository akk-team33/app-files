package de.team33.sphinx.gamma.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

/**
 * Basic {@link TableCellRenderer} implementation for a table header that is rendered by a JLabel.
 *
 * @param <C> A type that represents a {@linkplain Column column descriptor}.
 */
@SuppressWarnings("AbstractClassWithOnlyOneDirectInheritor")
public abstract class HeadRenderer<C extends CellRenderer.Column> extends CellRenderer<String, C> {

    /**
     *
     */
    protected HeadRenderer() {
        super(new JTable().getTableHeader().getDefaultRenderer(), String.class);
    }

    @Override
    protected final void setup(final JLabel result, final String value, final C column) {
        // nothing to do
    }
}
