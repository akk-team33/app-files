package net.team33.messaging.multiplex;

import net.team33.reflect.ClassUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Router<MSX> implements Relay<MSX> {
    private final Set<MSX> initials = new HashSet<>(0);
    private final REGISTRY registry = new REGISTRY();

    private synchronized boolean add(final Class<?> messageClass, final Consumer<?> listener) {
        return registry.get(messageClass).add(listener);
    }

    @Override
    public final void add(final Consumer<? extends MSX> listener) {
        final Class<?> msgClass = ClassUtil.getActualClassArgument(Consumer.class, listener.getClass());
        include(msgClass);
        if (add(msgClass, listener)) {
            init(msgClass, listener);
        }

    }

    public final synchronized void addInitial(final MSX initial) {
        initials.add(initial);
    }

    public final synchronized void removeInitial(final MSX initial) {
        initials.remove(initial);
    }

    private synchronized void include(final Class<?> messageClass) {
        if (!registry.containsKey(messageClass)) {
            registry.put(messageClass, new HashSet<>(0));
        }

    }

    @SuppressWarnings("rawtypes")
    private void init(final Class<?> messageClass, final Consumer lstnr) {

        for (final Object initial : tmpInitials()) {
            if (messageClass.isAssignableFrom(initial.getClass())) {
                lstnr.accept(initial);
            }
        }

    }

    private synchronized void remove(final Class<?> messageClass, final Consumer<?> listener) {
        registry.get(messageClass).remove(listener);
    }

    @Override
    public final void remove(final Consumer<? extends MSX> lstnr) {
        for (final Class<?> messageClass : tmpMessageClasses()) {
            remove(messageClass, lstnr);
        }
    }

    @Override
    public final void route(final MSX message) {
        for (final Class<?> messageClass : tmpMessageClasses()) {
            if (messageClass.isAssignableFrom(message.getClass())) {
                route(messageClass, message);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void route(final Class<?> messageClass, final Object message) {
        for (final Consumer consumer : tmpRegistry(messageClass)) {
            consumer.accept(message);
        }
    }

    private synchronized Set<Object> tmpInitials() {
        return new HashSet<>(initials);
    }

    private synchronized Set<Class<?>> tmpMessageClasses() {
        return new HashSet<>(registry.keySet());
    }

    private synchronized Set<Consumer<?>> tmpRegistry(final Class<?> messageClass) {
        return new HashSet<>(registry.get(messageClass));
    }

    private static class REGISTRY extends HashMap<Class<?>, Set<Consumer<?>>> {
    }
}
