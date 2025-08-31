package de.team33.sphinx.gamma.table;

import javax.swing.*;
import java.util.List;

/**
 * @param <V> The type of values to be rendered.
 * @param <C> A type that represents a column descriptor.
 */
public abstract class CellRenderer<V, C extends Column<?>> extends BaseRenderer<V, C> {

    /**
     * @param columns    An <b>immutable</b> {@link List} of column descriptors.
     * @param valueClass The {@link Class} representation of the type of values to be rendered.
     */
    public CellRenderer(final List<? extends C> columns, final Class<? extends V> valueClass) {
        super(new JTable().getDefaultRenderer(Object.class), columns, valueClass);
    }
}
