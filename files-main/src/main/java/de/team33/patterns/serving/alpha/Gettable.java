package de.team33.patterns.serving.alpha;

import java.util.function.Supplier;

/**
 * Represents a service component whose "content" can be determined.
 *
 * @param <C> The type of “content”.
 */
@FunctionalInterface
public interface Gettable<C> extends Supplier<C> {

    /**
     * Returns the "content" of <em>this</em> service component.
     * <p>
     * An implementation is expected to be atomic with respect to <em>this</em> service component.
     */
    @Override
    C get();
}
