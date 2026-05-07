package de.team33.patterns.serving.alpha;

import java.util.function.Consumer;

/**
 * Represents a service component that allows interested parties to
 * subscribe to state changes.
 * <p>
 * Subscribers are notified whenever a new state becomes available
 * as a result of a successful state transition.
 *
 * @param <C> the type of content
 * @see de.team33.patterns.serving.alpha package
 */
@FunctionalInterface
public interface Subscribable<C> {

    /**
     * Subscribes to state change notifications of this component.
     * <p>
     * The returned subscription can be used to cancel further notifications.
     *
     * @param listener a consumer receiving new state values
     * @return a subscription handle for cancellation
     */
    Subscription subscribe(Consumer<? super C> listener);
}