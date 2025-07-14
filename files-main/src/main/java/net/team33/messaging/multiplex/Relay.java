package net.team33.messaging.multiplex;

public interface Relay<MSX> extends Register<MSX> {
    void route(MSX var1);
}
