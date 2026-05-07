package de.team33.patterns.serving.alpha;

/**
 * Represents a fully mutable and observable service component.
 * <p>
 * This interface combines read, write, and subscription capabilities
 * into a single abstraction.
 * <p>
 * State updates are observable only after successful transitions.
 *
 * @param <C> the type of content
 */
public interface Variable<C> extends Mutable<C>, Retrievable<C>, Reflective<C> {
}