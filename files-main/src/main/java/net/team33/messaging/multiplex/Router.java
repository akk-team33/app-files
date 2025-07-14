package net.team33.messaging.multiplex;

import net.team33.messaging.Listener;
import net.team33.reflect.ClassUtil;

import java.util.*;

public class Router<MSX> implements Relay<MSX> {
    private final Set<MSX> initials = new HashSet();
    private final REGISTRY registry = new REGISTRY();

    private synchronized boolean add(Class<?> messageClass, Listener<?> listener) {
        return ((Set)this.registry.get(messageClass)).add(listener);
    }

    @Override
    public final void add(Listener<? extends MSX> listener) {
        Class<?> msgClass = ClassUtil.getActualClassArgument(Listener.class, listener.getClass());
        this.include(msgClass);
        if (this.add(msgClass, listener)) {
            this.init(msgClass, listener);
        }

    }

    public final synchronized void addInitial(MSX initial) {
        this.initials.add(initial);
    }

    public final synchronized void removeInitial(MSX initial) {
        this.initials.remove(initial);
    }

    private synchronized void include(Class<?> messageClass) {
        if (!this.registry.containsKey(messageClass)) {
            this.registry.put(messageClass, new HashSet());
        }

    }

    private void init(Class messageClass, Listener lstnr) {
        Iterator var4 = this.tmpInitials().iterator();

        while(var4.hasNext()) {
            Object initial = var4.next();
            if (messageClass.isAssignableFrom(initial.getClass())) {
                lstnr.pass(initial);
            }
        }

    }

    private synchronized void remove(Class<?> messageClass, Listener<?> listener) {
        ((Set)this.registry.get(messageClass)).remove(listener);
    }

    @Override
    public final void remove(Listener<? extends MSX> lstnr) {
        Iterator var3 = this.tmpMessageClasses().iterator();

        while(var3.hasNext()) {
            Class<?> msgClass = (Class)var3.next();
            this.remove(msgClass, lstnr);
        }

    }

    @Override
    public final void route(MSX message) {
        Iterator var3 = this.tmpMessageClasses().iterator();

        while(var3.hasNext()) {
            Class<?> messageClass = (Class)var3.next();
            if (messageClass.isAssignableFrom(message.getClass())) {
                this.route(messageClass, message);
            }
        }

    }

    private void route(Class<?> messageClass, Object message) {
        Iterator var4 = this.tmpRegistry(messageClass).iterator();

        while(var4.hasNext()) {
            Listener listener = (Listener)var4.next();
            listener.pass(message);
        }

    }

    private synchronized Set<Object> tmpInitials() {
        return new HashSet(this.initials);
    }

    private synchronized Set<Class<?>> tmpMessageClasses() {
        return new HashSet((Collection)this.registry.keySet());
    }

    private synchronized Set<Listener<?>> tmpRegistry(Class<?> messageClass) {
        return new HashSet((Collection)this.registry.get(messageClass));
    }

    private static class REGISTRY extends HashMap<Class<?>, Set<Listener<?>>> {
    }
}
