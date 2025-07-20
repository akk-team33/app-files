package de.team33.patterns.serving.alpha;

import java.util.function.Consumer;

/**
 * Represents a service component that allows interested parties to subscribe
 * and receive newly emerging "content".
 *
 * @param <C> The type of “content”.
 */
@FunctionalInterface
public interface Subscribable<C> {

    /**
     * Subscribes <em>this</em> service component for newly emerging "content".
     *
     * @param listener A {@link Consumer} that will receive newly emerging "content".
     * @return A {@link Subscription} that can be used to {@linkplain Subscription#cancel() cancel} receiving
     * newly emerging "content" or may be ignored.
     */
    Subscription subscribe(Consumer<? super C> listener);
}
