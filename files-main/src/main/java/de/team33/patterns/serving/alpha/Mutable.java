package de.team33.patterns.serving.alpha;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents a service component whose "content" can be determined and redefined.
 *
 * @param <C> The type of “content”.
 */
public interface Mutable<C> extends Gettable<C>, Settable<C> {

    /**
     * Creates a new {@link Mutable} by joining a {@link Supplier} and a {@link Consumer}.
     * <p>
     * No guarantee for consistent behavior of the result!
     *
     * @param <C> The type of “content”.
     */
    static <C> Mutable<C> join(final Supplier<? extends C> gettable, final Consumer<? super C> settable) {
        return new Mutable<C>() {
            @Override
            public final C get() {
                return gettable.get();
            }

            @Override
            public final void set(final C content) {
                settable.accept(content);
            }
        };
    }
}
