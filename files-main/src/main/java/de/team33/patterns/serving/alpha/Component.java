package de.team33.patterns.serving.alpha;

import java.util.concurrent.Executor;
import java.util.function.UnaryOperator;

public class Component<C> extends Audience<C> implements Variable<C> {

    private final UnaryOperator<C> normalizer;
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
        super(executor);
        this.normalizer = normalizer;
        this.content = normalizer.apply(content);
    }

    @Override
    public final C get() {
        return content;
    }

    @Override
    public final void set(final C content) {
        fire(setNormal(normalizer.apply(content)));
    }

    private C setNormal(final C normalContent) {
        this.content = normalContent;
        return normalContent;
    }
}
