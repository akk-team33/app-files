//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.messaging.multiplex;

import net.team33.messaging.Listener;

public interface Register<MSX> {
    void add(Listener<? extends MSX> var1);

    void remove(Listener<? extends MSX> var1);
}
