package de.team33.sphinx.gamma.table;

import java.util.Comparator;

/**
 * Represents a basic type of data to be displayed within a specific table column.
 * Can also serve as a row sorting criterion for a table in question.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 *
 * @param <P> The final type of data to be displayed within a specific table column.
 * @see #equals(ColumnProperty, Object)
 */
@SuppressWarnings("AbstractClassWithOnlyOneDirectInheritor")
public abstract class ColumnProperty<P extends ColumnProperty<P>> implements Comparable<P> {

    private static final String ILLEGAL_FINAL_CLASS =
            "<finalClass> is expected to represent the class of <this> (%s) - but was %s";

    private final Class<? extends P> finalClass;
    private final Comparator<? super P> order;

    /**
     * Initializes a new instance and checks the final type for consistency.
     *
     * @param finalClass The {@link Class} representation of the intended final type.
     * @param order      The sorting criterion intended for the final type.
     */
    protected ColumnProperty(final Class<? extends P> finalClass, final Comparator<? super P> order) {
        //noinspection ThisEscapedInObjectConstruction
        if (finalClass.isInstance(this)) {
            this.finalClass = finalClass;
            this.order = order;
        } else {
            throw new IllegalArgumentException(ILLEGAL_FINAL_CLASS.formatted(getClass(), finalClass));
        }
    }

    /**
     * Utility method to support an implementation of {@link #equals(Object)} that behaves consistently with
     * {@link #compareTo(ColumnProperty)}.
     * <p>
     * It's important to remember that {@link #hashCode()} must also behave appropriately!
     *
     * @param <P> The final type of {@link ColumnProperty}.
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public static <P extends ColumnProperty<P>> boolean equals(final ColumnProperty<P> property, final Object other) {
        return (property == other) ||
               (property.finalClass.isInstance(other) && (0 == property.compareTo(property.finalClass.cast(other))));
    }

    @Override
    public final int compareTo(final P other) {
        return order.compare(finalClass.cast(this), other);
    }

    /**
     * Returns the text to be displayed in an underlying table.
     */
    @Override
    public abstract String toString();
}
