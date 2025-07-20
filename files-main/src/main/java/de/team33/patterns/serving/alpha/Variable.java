package de.team33.patterns.serving.alpha;

import java.util.function.Consumer;

/**
 * Represents a service component whose "content" can be determined, redefined
 * and that allows interested parties to subscribe and receive newly emerging "content".
 *
 * @param <C> The type of “content”.
 */
public interface Variable<C> extends Retrievable<C>, Mutable<C> {

    /**
     * Creates a new {@link Variable} by joining a {@link Gettable}, a {@link Subscribable} and a {@link Settable}.
     * <p>
     * No guarantee for consistent behavior of the result!
     *
     * @param <C> The type of “content”.
     */
    static <C> Variable<C> join(final Gettable<? extends C> gettable,
                                final Subscribable<? extends C> subscribable,
                                final Settable<? super C> settable) {
        return new Variable<C>() {

            @Override
            public final C get() {
                return gettable.get();
            }

            @Override
            public final void set(final C content) {
                settable.set(content);
            }

            @Override
            public final Subscription subscribe(final Consumer<? super C> listener) {
                return subscribable.subscribe(listener);
            }
        };
    }
}
