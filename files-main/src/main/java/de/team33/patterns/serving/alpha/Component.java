package de.team33.patterns.serving.alpha;

import de.team33.patterns.exceptional.dione.XFunction;

import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static java.lang.System.Logger.Level.DEBUG;

public class Component<C> extends Audience<C> implements Variable<C> {

    private static final System.Logger LOGGER = System.getLogger(Component.class.getCanonicalName());

    private final XFunction<? super C, ? extends C, ? extends SetException> normalizer;
    private volatile C content;

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
        super(executor);
        try {
            this.normalizer = normalizer;
            this.content = normalizer.apply(content);
        } catch (final SetException e) {
            throw new IllegalArgumentException("illegal initial content: '%s'".formatted(content), e);
        }
    }

    @Override
    public final C get() {
        return atomic(() -> content);
    }

    @Override
    public final void set(final C content) {
        if (this.content != content) {
            fire(atomic(() -> setNormal(content)));
        }
    }

    @SuppressWarnings("ParameterHidesMemberVariable")
    private C setNormal(final C content) {
        try {
            this.content = normalizer.apply(content);
        } catch (final SetException e) {
            LOGGER.log(DEBUG, e::getMessage, e);
        }
        return this.content;
    }

    @SuppressWarnings("SynchronizedMethod")
    private synchronized <R> R atomic(final Supplier<R> supplier) {
        return supplier.get();
    }

    public static class SetException extends Exception {

    }
}