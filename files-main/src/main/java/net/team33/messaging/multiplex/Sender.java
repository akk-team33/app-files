//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.messaging.multiplex;

public class Sender<MSX> implements Originator<MSX> {
    private Router<MSX> router = new Router();

    public Sender() {
    }

    public Register<MSX> getRegister() {
        return this.router;
    }

    protected void addInitial(MSX initial) {
        this.router.addInitial(initial);
    }

    protected void removeInitial(MSX initial) {
        this.router.removeInitial(initial);
    }

    protected void fire(MSX message) {
        this.router.route(message);
    }
}
