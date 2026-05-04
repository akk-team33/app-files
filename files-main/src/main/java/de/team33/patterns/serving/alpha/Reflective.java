package de.team33.patterns.serving.alpha;

/**
 * Represents a service component whose "content" can be redefined
 * and that allows interested parties to subscribe and receive newly emerging "content".
 *
 * @param <C> The type of “content”.
 * @see de.team33.patterns.serving.alpha package
 */
public interface Reflective<C> extends Settable<C>, Subscribable<C> {
}