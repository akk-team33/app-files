/**
 * Provides a lightweight model for event-driven service components.
 * <p>
 * The model is based on three orthogonal capabilities:
 * <ul>
 *   <li>state access (Gettable)</li>
 *   <li>state mutation (Settable)</li>
 *   <li>state observation (Subscribable)</li>
 * </ul>
 * <p>
 * A service component is modeled as a stateful entity whose state
 * evolves through explicit transitions. Successful transitions may
 * be observed by interested parties.
 * <p>
 * The framework does not impose a global ordering of events across
 * concurrent transitions.
 */
package de.team33.patterns.serving.alpha;