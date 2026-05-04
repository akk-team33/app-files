package de.team33.patterns.serving.alpha;

class MutableSimple<C> implements Mutable<C> {

    private volatile C content;

    MutableSimple(final C content) {
        this.content = content;
    }

    @Override
    public final C get() {
        return content;
    }

    @Override
    public final void set(final C content) {
        this.content = content;
    }
}
