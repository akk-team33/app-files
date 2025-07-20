package de.team33.patterns.serving.alpha;

import java.util.function.Consumer;

/**
 * Represents a service component whose "content" can be determined
 * and that allows interested parties to subscribe and receive newly emerging "content".
 *
 * @param <C> The type of “content”.
 */
public interface Retrievable<C> extends Gettable<C>, Subscribable<C> {

    /**
     * Creates a new {@link Retrievable} by joining a {@link Gettable} and a {@link Subscribable}.
     * <p>
     * No guarantee for consistent behavior of the result!
     *
     * @param <C> The type of “content”.
     */
    static <C> Retrievable<C> join(final Gettable<? extends C> gettable,
                                   final Subscribable<? extends C> subscribable) {
        return new Retrievable<C>() {
            @Override
            public C get() {
                return gettable.get();
            }

            @Override
            public Subscription subscribe(final Consumer<? super C> listener) {
                return subscribable.subscribe(listener);
            }
        };
    }

    /**
     * Retrieves the current “content” of <em>this</em> service component and
     * subscribes it for future emerging "content".
     *
     * @param listener A {@link Consumer} that will receive current and future emerging "content".
     * @return A {@link Subscription} that can be used to {@linkplain Subscription#cancel() cancel} receiving
     * newly emerging "content" or may be ignored.
     */
    default Subscription retrieve(final Consumer<? super C> listener) {
        listener.accept(get());
        return subscribe(listener);
    }
}
