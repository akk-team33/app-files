package de.team33.patterns.serving.alpha;

import java.util.function.Consumer;

/**
 * Represents a read-only service component that can also emit state changes
 * to interested subscribers.
 *
 * <p>Implementations provide access to the current state as well as
 * notifications of future state changes.</p>
 *
 * @param <C> the type of content
 * @see de.team33.patterns.serving.alpha package
 */
public interface Retrievable<C> extends Gettable<C>, Subscribable<C> {

    /**
     * Subscribes with a specific mode controlling initial state delivery.
     *
     * @param mode     subscription mode controlling initial notification behavior
     * @param listener receives current and future state values
     * @return a subscription handle
     */
    default Subscription subscribe(final Mode mode, final Consumer<? super C> listener) {
        return mode.subscribe(this, listener);
    }

    /**
     * Defines subscription initialization behavior.
     */
    enum Mode {

        /**
         * Immediately delivers the current state before subscribing to updates.
         * <p>
         * The initial state delivery and the subsequent subscription are not atomic
         * with respect to concurrent state transitions.
         */
        INIT(true),

        /**
         * Subscribes only to future state changes.
         */
        NEXT(false);

        private final boolean init;

        Mode(final boolean init) {
            this.init = init;
        }

        private <C> Subscription subscribe(final Retrievable<C> retrievable, final Consumer<? super C> listener) {
            if (init) {
                listener.accept(retrievable.get());
            }
            return retrievable.subscribe(listener);
        }
    }
}