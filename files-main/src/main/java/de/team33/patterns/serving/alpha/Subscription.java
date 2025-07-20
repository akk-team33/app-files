package de.team33.patterns.serving.alpha;

import java.util.function.Consumer;

/**
 * Represents a subscription of a service component.
 *
 * @see Subscribable#subscribe(Consumer)
 */
@FunctionalInterface
public interface Subscription {

    /**
     * Cancels <em>this</em> {@link Subscription}.
     */
    void cancel();
}
