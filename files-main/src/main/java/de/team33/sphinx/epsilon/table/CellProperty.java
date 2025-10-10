package de.team33.sphinx.epsilon.table;

import de.team33.patterns.building.anthe.SelfReferring;

import java.util.Comparator;

/**
 * Represents a basic type of data to be displayed within a specific table column.
 * Can also serve as a row sorting criterion for a table in question.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 *
 * @param <P> The final type of data to be displayed within a specific table column.
 * @see #equals(CellProperty, Object)
 */
@SuppressWarnings({"AbstractClassWithOnlyOneDirectInheritor", "AbstractClassExtendsConcreteClass"})
public abstract class CellProperty<P extends CellProperty<P>> extends SelfReferring<P> implements Comparable<P> {

    private final Class<P> finalClass;
    private final Comparator<? super P> order;

    /**
     * Initializes a new instance and checks the final type for consistency.
     *
     * @param finalClass The {@link Class} representation of the intended final type.
     * @param order      The sorting criterion intended for the final type.
     */
    protected CellProperty(final Class<P> finalClass, final Comparator<? super P> order) {
        super(finalClass);
        this.finalClass = finalClass;
        this.order = order;
    }

    /**
     * Utility method to support an implementation of {@link #equals(Object)} that behaves consistently with
     * {@link #compareTo(CellProperty)}.
     * <p>
     * It's important to remember that {@link #hashCode()} must also behave appropriately!
     *
     * @param <P> The final type of {@link CellProperty}.
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public static <P extends CellProperty<P>> boolean equals(final CellProperty<P> property, final Object other) {
        return (property == other) ||
               (property.finalClass.isInstance(other) && (0 == property.compareTo(property.finalClass.cast(other))));
    }

    @Override
    public final int compareTo(final P other) {
        return order.compare(THIS(), other);
    }

    /**
     * Returns the text to be displayed in an underlying table.
     */
    @Override
    public abstract String toString();
}