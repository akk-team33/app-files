package de.team33.patterns.serving.alpha.publics;

import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Gettable;
import de.team33.patterns.serving.alpha.Subscription;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static de.team33.patterns.serving.alpha.Retrievable.Mode.NEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentTest {

    private static final String[] STRING = Stream.generate(() -> UUID.randomUUID().toString())
                                                 .limit(3)
                                                 .toArray(String[]::new);

    @Test
    final void subscribe_NEXT() {
        final Component<String> component = new Component<>(STRING[0]);
        final List<String> target = new LinkedList<>();

        component.subscribe(NEXT, target::add);
        assertEquals(List.of(), target, "<target> is still expected to be empty");

        component.set(STRING[1]);
        assertEquals(List.of(STRING[1]), target, "<target> is expected to be updated");

        component.set(STRING[2]);
        assertEquals(List.of(STRING[1], STRING[2]), target, "<target> is expected to be updated again");
    }

    @Test
    final void subscribe_INIT() {
        final Component<String> component = new Component<>(Runnable::run, STRING[0]);
        final List<String> target = new LinkedList<>();

        final Subscription subscription = component.subscribe(INIT, target::add);
        assertEquals(List.of(STRING[0]), target, "<target> is expected to contain STRING[0]");

        component.set(STRING[1]);
        assertEquals(List.of(STRING[0], STRING[1]), target, "<target> is expected to be updated");

        subscription.cancel();
        component.set(STRING[2]);
        assertEquals(List.of(STRING[0], STRING[1]), target, "<target> is not expected to be updated again");
    }

    @Test
    final void get() {
        final Gettable<String> component = new Component<>(UnaryOperator.identity(), STRING[0]);
        assertEquals(STRING[0], component.get());
    }

    @Test
    final void set() {
        final Component<String> component = new Component<>(Runnable::run, UnaryOperator.identity(), STRING[0]);
        component.set(STRING[1]);
        assertEquals(STRING[1], component.get());
    }
}