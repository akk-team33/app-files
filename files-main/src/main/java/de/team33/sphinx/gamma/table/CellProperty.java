package de.team33.sphinx.gamma.table;

import java.util.Comparator;

@SuppressWarnings("AbstractClassWithOnlyOneDirectInheritor")
public abstract class CellProperty<P extends CellProperty<P>> implements Comparable<P> {

    private final Class<P> finalClass;
    private final Comparator<P> order;

    protected CellProperty(final Class<P> finalClass, final Comparator<P> order) {
        this.finalClass = finalClass;
        this.order = order;
        // fast fail if so ...
        finalClass.cast(this);
    }

    @Override
    public final int compareTo(final P other) {
        return order.compare(finalClass.cast(this), other);
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || (finalClass.isInstance(obj) && (0 == compareTo(finalClass.cast(obj))));
    }

    @Override
    public final int hashCode() {
        return hashCriterion().hashCode();
    }

    protected abstract Object hashCriterion();

    @Override
    public abstract String toString();
}
