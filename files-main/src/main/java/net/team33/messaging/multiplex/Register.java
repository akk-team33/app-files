package net.team33.messaging.multiplex;

import net.team33.messaging.Listener;

public interface Register<MSX> {
    void add(Listener<? extends MSX> var1);

    void remove(Listener<? extends MSX> var1);
}
