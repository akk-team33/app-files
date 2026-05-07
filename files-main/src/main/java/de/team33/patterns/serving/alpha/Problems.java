package de.team33.patterns.serving.alpha;

import java.util.LinkedList;
import java.util.List;

class Problems<X extends Throwable> {

    private final List<X> backing = new LinkedList<>();

    private static <X extends Throwable> X addSuppressed(final X left, final X right) {
        left.addSuppressed(right);
        return left;
    }

    final void add(final X exception) {
        backing.add(exception);
    }

    final void throwIfPresent() throws X {
        if (0 < backing.size()) {
            throw backing.stream().skip(1)
                         .reduce(backing.get(0), Problems::addSuppressed);
        }
    }
}
