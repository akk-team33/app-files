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
     * Subscribes <em>this</em> service component for (current and) newly emerging "content".
     *
     * @param mode     A {@link Mode} that controls the subscription process.
     * @param listener A {@link Consumer} that will receive (current and) newly emerging "content".
     * @return A {@link Subscription} that can be used to {@linkplain Subscription#cancel() cancel} receiving
     * newly emerging "content" or may be ignored.
     * @see Subscribable#subscribe(Consumer)
     */
    default Subscription subscribe(final Mode mode, final Consumer<? super C> listener) {
        mode.runner.accept(() -> listener.accept(get()));
        return subscribe(listener);
    }

    /**
     * Defines different subscription modes.
     */
    enum Mode {

        /**
         * Causes a listener to be instantly notified of the current state of the service component in question.
         * This will typically cause an initialization of the listening component
         */
        INIT(Runnable::run),

        /**
         * Causes a {@linkplain Consumer listener} to be notified of the state of the service component in question
         * for the first time at the next regular event.
         * <p>
         * This corresponds to the behavior of {@link Subscribable#subscribe(Consumer)}.
         */
        NEXT(Mode::skip);

        private final Consumer<Runnable> runner;

        Mode(final Consumer<Runnable> runner) {
            this.runner = runner;
        }

        private static void skip(final Runnable runnable) {
            // nothing to do!
        }
    }
}
