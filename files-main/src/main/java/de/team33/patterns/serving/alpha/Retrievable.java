package de.team33.patterns.serving.alpha;

import java.util.function.Consumer;

/**
 * Represents a service component whose "content" can be determined
 * and that allows interested parties to subscribe and receive newly emerging "content".
 *
 * @param <C> The type of “content”.
 * @see de.team33.patterns.serving.alpha package
 */
public interface Retrievable<C> extends Gettable<C>, Subscribable<C> {

    /**
     * Subscribes <em>this</em> service component for (current and) newly emerging "content".
     *
     * @param mode     A {@link Mode} that controls the subscription process.
     * @param listener A {@link Consumer} that will receive (current and) newly emerging "content".
     * @return A {@link Subscription} that can be used to {@linkplain Subscription#cancel() cancel} receiving
     * newly emerging "content" or may be ignored.
     * @see Subscribable#subscribe(Consumer)
     */
    default Subscription subscribe(final Mode mode, final Consumer<? super C> listener) {
        return mode.subscribe(this, listener);
    }

    /**
     * Defines different subscription modes.
     */
    enum Mode {

        /**
         * Causes a listener to be instantly notified of the current state of the service component in question.
         * This will typically cause an initialization of the listening component
         */
        INIT(true),

        /**
         * Causes a {@linkplain Consumer listener} to be notified of the state of the service component in question
         * for the first time at the next regular event.
         * <p>
         * This corresponds to the behavior of {@link Subscribable#subscribe(Consumer)}.
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