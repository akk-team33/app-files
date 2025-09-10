package de.team33.sphinx.delta.table;

import javax.swing.table.TableModel;
import java.util.Comparator;

/**
 * Represents a type of data to be displayed within a table cell that belongs to a specific column.
 * <p>
 * The following conditions apply:
 * <ul>
 *     <li>This class or a derivative is meant as the result of {@link TableModel#getColumnClass(int)}, so that
 *     {@link TableModel#getValueAt(int, int)} of that model will return a corresponding instance.</li>
 *     <li>The {@link #toString()} is meant to returns the text to be displayed in the table cell in question.</li>
 *     <li>Can serve as a row sorting criterion for a table in question</li>
 *     <li>The implementation of {@link #compareTo(CellProperty)} is specifically designed for use in the context of a
 *     {@link TableModel} and deviates from the general specification!</li>
 * </ul>
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 *
 * @param <R> The type that represents the content of a table row;
 * @see #equals(CellProperty, Object)
 */
public class CellProperty<R, C extends CellProperty.Column<R>> implements Comparable<CellProperty<R, C>> {

    private final R rowContent;
    private final C column;

    public CellProperty(final R rowContent, final C column) {
        this.rowContent = rowContent;
        this.column = column;
    }

    /**
     * Utility method to support an implementation of {@link #equals(Object)} that behaves consistently with
     * {@link #compareTo(CellProperty)}.
     * <p>
     * It's important to remember that {@link #hashCode()} must also behave appropriately!
     */
    @SuppressWarnings({"WeakerAccess", "rawtypes", "unchecked"})
    public static <R, C extends Column<R>> boolean equals(final CellProperty<R, C> prop, final Object obj) {
        try {
            return (prop == obj) || ((obj instanceof final CellProperty other) && (0 == prop.compareTo(other)));
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Returns the content of the table row <em>this</em> cell belongs to.
     */
    public final R rowContent() {
        return rowContent;
    }

    /**
     * Returns a constant that identifies the table column <em>this</em> property belongs to.
     */
    @SuppressWarnings("WeakerAccess")
    public final C column() {
        return column;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note: different behavior:</b> Comparing <em>this</em> instance with another one only works
     * if both belong to the same table column, i.e., if {@link #column()} returns the same constant.
     * Otherwise, an {@link IllegalArgumentException} is thrown!
     *
     * @throws IllegalArgumentException If <em>other</em> does not belong to the same column as <em>this</em>.
     */
    @Override
    public final int compareTo(final CellProperty<R, C> other) throws IllegalArgumentException {
        if (column() == other.column()) {
            return column.order().compare(rowContent, other.rowContent);
        } else {
            throw new IllegalArgumentException("<other> should belong to column %s, but belongs to %s".formatted(
                    column(), other.column()));
        }
    }

    @Override
    public final String toString() {
        return column.toString(rowContent);
    }

    /**
     * Represents a table column description and identification to be used in the context of a {@link CellProperty}.
     *
     * @param <R> The type that represents the content of a table row;
     */
    @SuppressWarnings("InterfaceNeverImplemented")
    public interface Column<R> {

        /**
         * Returns the {@link Comparator sort order} of table row contents with respect to <em>this</em> table column.
         */
        Comparator<R> order();

        /**
         * Returns the string representation of a given <em>rowContent</em> relative to <em>this</em> table column.
         */
        String toString(R rowContent);
    }
}
