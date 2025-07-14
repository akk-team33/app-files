package net.team33.messaging.multiplex;

import java.util.function.Consumer;

public interface Register<MSX> {
    void add(Consumer<? extends MSX> var1);

    void remove(Consumer<? extends MSX> var1);
}
