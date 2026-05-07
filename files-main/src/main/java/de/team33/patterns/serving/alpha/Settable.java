package de.team33.patterns.serving.alpha;

/**
 * Represents a state transition capability of a service component.
 * <p>
 * An invocation of {@code set} attempts to transition the component's
 * current state to the given content.
 * <p>
 * The operation is atomic with respect to the component state.
 * <p>
 * The result indicates whether the transition was successfully applied:
 * <ul>
 *   <li>{@code true} — the state was updated</li>
 *   <li>{@code false} — the state remained unchanged</li>
 * </ul>
 * <p>
 * No further side effects are defined by this contract.
 *
 * @param <C> the type of content
 * @see de.team33.patterns.serving.alpha package
 */
@FunctionalInterface
public interface Settable<C> {

    /**
     * Redefines the "content" of <em>this</em> service component.
     * <p>
     * An implementation is expected to be atomic with respect to <em>this</em> service component.
     */
    void set(C content);
}