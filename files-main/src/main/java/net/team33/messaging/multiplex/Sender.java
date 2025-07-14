package net.team33.messaging.multiplex;

public class Sender<MSX> implements Originator<MSX> {
    private final Router<MSX> router = new Router();

    @Override
    public final Register<MSX> getRegister() {
        return router;
    }

    protected final void addInitial(final MSX initial) {
        router.addInitial(initial);
    }

    protected final void removeInitial(final MSX initial) {
        router.removeInitial(initial);
    }

    protected final void fire(final MSX message) {
        router.route(message);
    }
}
