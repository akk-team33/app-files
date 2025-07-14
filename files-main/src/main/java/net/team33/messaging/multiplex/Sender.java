package net.team33.messaging.multiplex;

public class Sender<MSX> implements Originator<MSX> {
    private final Router<MSX> router = new Router();

    @Override
    public final Register<MSX> getRegister() {
        return this.router;
    }

    protected final void addInitial(MSX initial) {
        this.router.addInitial(initial);
    }

    protected final void removeInitial(MSX initial) {
        this.router.removeInitial(initial);
    }

    protected final void fire(MSX message) {
        this.router.route(message);
    }
}
