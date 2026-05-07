package de.team33.patterns.serving.alpha;

/**
 * Represents a mutable service component whose state can be read and updated.
 * <p>
 * This interface combines read and write access to a component state.
 * <p>
 * Thread-safety depends on the concrete implementation.
 *
 * @param <C> the type of content
 * @see de.team33.patterns.serving.alpha package
 */
public interface Mutable<C> extends Gettable<C>, Settable<C> {

    /**
     * Returns a simple mutable implementation backed by an in-memory value.
     * <p>
     * The returned instance is thread-safe only if the contained value is immutable.
     */
    static <C> Mutable<C> simple(final C content) {
        return new MutableSimple<>(content);
    }
}