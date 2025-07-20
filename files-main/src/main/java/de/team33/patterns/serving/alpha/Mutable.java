package de.team33.patterns.serving.alpha;

/**
 * Represents a service component whose "content" can be determined and redefined.
 *
 * @param <C> The type of “content”.
 */
public interface Mutable<C> extends Gettable<C>, Settable<C> {

    /**
     * Creates a new {@link Mutable} by joining a {@link Gettable} and a {@link Settable}.
     * <p>
     * No guarantee for consistent behavior of the result!
     *
     * @param <C> The type of “content”.
     */
    static <C> Mutable<C> join(final Gettable<? extends C> gettable, final Settable<? super C> settable) {
        return new Mutable<C>() {
            @Override
            public final C get() {
                return gettable.get();
            }

            @Override
            public final void set(final C content) {
                settable.set(content);
            }
        };
    }
}
