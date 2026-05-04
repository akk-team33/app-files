package de.team33.patterns.serving.alpha;

/**
 * Represents a service component whose "content" can be determined and redefined.
 *
 * @param <C> The type of “content”.
 * @see de.team33.patterns.serving.alpha package
 */
public interface Mutable<C> extends Gettable<C>, Settable<C> {

    /**
     * Returns a simple {@link Mutable} with a given initial <em>content</em>.
     * <p>
     * The result ist thread-safe only if {@code <C>} is immutable.
     */
    static <C> Mutable<C> simple(final C content) {
        return new MutableSimple<>(content);
    }
}