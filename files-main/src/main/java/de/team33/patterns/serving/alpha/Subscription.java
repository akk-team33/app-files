package de.team33.patterns.serving.alpha;

/**
 * Represents a registration for receiving state change notifications
 * from a service component.
 * <p>
 * A subscription can be cancelled at any time, after which no further
 * notifications will be delivered to the associated listener.
 */
@FunctionalInterface
public interface Subscription {

    /**
     * Cancels this subscription.
     * <p>
     * After cancellation, no further notifications are guaranteed.
     */
    void cancel();
}