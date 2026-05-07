package de.team33.patterns.serving.alpha;

import de.team33.patterns.exceptional.dione.XFunction;

import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static java.lang.System.Logger.Level.WARNING;

/**
 * Listener notifications are dispatched asynchronously according to the
 * configured Executor.
 * <p>
 * Consequently, listeners may observe notifications for previous values
 * after the component state has already advanced further.
 * <p>
 * Listeners should therefore treat the event value as the authoritative
 * snapshot associated with the notification.
 */
public class Component<C> implements Variable<C> {

    private static final System.Logger LOGGER = System.getLogger(Component.class.getCanonicalName());

    private final XFunction<? super C, ? extends C, ? extends SetException> normalizer;
    private final Audience<C> audience;
    private final Mutable<C> mutable;

    public Component(final C content) {
        this(Runnable::run, content);
    }

    public Component(final Executor executor, final C content) {
        this(executor, UnaryOperator.identity(), content);
    }

    public Component(final UnaryOperator<C> normalizer, final C content) {
        this(Runnable::run, normalizer, content);
    }

    public Component(final Executor executor, final UnaryOperator<C> normalizer, final C content) {
        this(executor, content, normalizer::apply);
    }

    public Component(final Executor executor, final C content,
                     final XFunction<? super C, ? extends C, ? extends SetException> normalizer) {
        this.normalizer = normalizer;
        this.audience = new Audience<>(executor);
        try {
            this.mutable = new MutableSimple<>(normalizer.apply(content));
        } catch (final SetException e) {
            throw new IllegalArgumentException("illegal initial content: '%s'".formatted(content), e);
        }
    }

    @Override
    public final C get() {
        return mutable.get();
    }

    @Override
    public final void set(final C content) {
        try {
            setNormal(normalizer.apply(content));
        } catch (final SetException e) {
            LOGGER.log(WARNING, e::getMessage, e);
        }
    }

    private void setNormal(final C content) {
        mutable.set(content);
        audience.fire(content);
    }

    @Override
    public final Subscription subscribe(final Consumer<? super C> listener) {
        return audience.subscribe(listener);
    }

    @SuppressWarnings("unused")
    public static class SetException extends Exception {

        public SetException(final String message) {
            super(message);
        }

        public SetException(final Throwable cause) {
            super(cause.getMessage(), cause);
        }

        public SetException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}