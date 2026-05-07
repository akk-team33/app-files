package de.team33.patterns.serving.alpha;

/**
 * Represents a service component that emits state changes when its state
 * is modified.
 * <p>
 * State changes are only emitted after successful transitions.
 *
 * @param <C> the type of content
 */
public interface Reflective<C> extends Settable<C>, Subscribable<C> {
}