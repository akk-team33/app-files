package de.team33.patterns.serving.alpha;

/**
 * Represents a read-only access capability to a service component's content.
 * <p>
 * The returned value represents a consistent snapshot of the current state of the
 * component at the time of invocation.
 * <p>
 * Implementations are expected to provide atomic and thread-safe access
 * to the underlying state of the component.
 * <p>
 * This interface does not define any relationship to functional or
 * side-effect-free computation models such as {@link java.util.function.Supplier}.
 *
 * @param <C> the type of content being exposed
 * @see de.team33.patterns.serving.alpha package
 */
@FunctionalInterface
public interface Gettable<C> {

    /**
     * Returns the "content" of <em>this</em> service component.
     * <p>
     * An implementation is expected to be atomic with respect to <em>this</em> service component.
     */
    C get();
}