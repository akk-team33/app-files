package de.team33.patterns.serving.alpha;

import java.util.function.Consumer;

/**
 * Represents a service component whose “content” can be redefined.
 *
 * @param <C> The type of “content”.
 */
@FunctionalInterface
public interface Settable<C> extends Consumer<C> {

    /**
     * Redefines the "content" of <em>this</em> service component.
     * <p>
     * An implementation is expected to be atomic with respect to <em>this</em> service component.
     */
    void set(C content);

    @Override
    default void accept(final C content) {
        set(content);
    }
}
