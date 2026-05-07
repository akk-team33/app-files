package de.team33.patterns.serving.alpha;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

/**
 * Implementation of a {@link Subscribable} with the additional option to send messages to subscribers.
 * <p>
 * Asynchronously* dispatches messages to subscribed listeners.
 * <p>
 * This class is thread-safe.
 * <p>
 * Each invocation of {@link #fire(Object)} dispatches the message to a
 * stable snapshot of the listeners that were subscribed at the time the
 * dispatch began.
 * <p>
 * Concurrent subscription changes do not affect an already started
 * dispatch.
 * <p>
 * No guarantees are made regarding:
 * <ul>
 *   <li>ordering between concurrent dispatches,</li>
 *   <li>whether a listener subscribed concurrently with a dispatch
 *       will observe that dispatch,</li>
 *   <li>serialization of listener invocations, or</li>
 *   <li>the thread used for listener invocation.</li>
 * </ul>
 * <p>
 * *Listener invocation behavior depends on the configured {@link Executor}.
 *
 * @param <C> The type of “content”.
 */
@SuppressWarnings({"unused", "SynchronizedMethod", "WeakerAccess"})
public class Audience<C> implements Subscribable<C> {

    private final Executor executor;
    private volatile List<Consumer<? super C>> backing = List.of();

    public Audience(final Executor executor) {
        this.executor = executor;
    }

    private static <M> Runnable emitter(final Iterable<? extends Consumer<? super M>> listeners, final M message) {
        return () -> emit(listeners, message);
    }

    private static <M> void emit(final Iterable<? extends Consumer<? super M>> listeners, final M message) {
        final Problems<RuntimeException> problems = new Problems<>();
        for (final Consumer<? super M> listener : listeners) {
            try {
                listener.accept(message);
            } catch (final RuntimeException e) {
                problems.add(e);
            }
        }
        problems.throwIfPresent();
    }

    @Override
    public final synchronized Subscription subscribe(final Consumer<? super C> listener) {
        backing = Stream.concat(backing.stream(), Stream.of(listener))
                        .toList();
        return () -> unsubscribe(listener);
    }

    private synchronized void unsubscribe(final Consumer<? super C> listener) {
        backing = backing.stream()
                         .filter(not(listener::equals))
                         .toList();
    }

    private Optional<Runnable> emitter(final C message) {
        return Optional.of(backing)
                       .filter(not(List::isEmpty))
                       .map(listeners -> emitter(listeners, message));
    }

    /**
     * Sends a given message to all listeners that have {@linkplain #subscribe(Consumer) subscribed}.
     */
    public final void fire(final C message) {
        emitter(message).ifPresent(executor::execute);
    }
}