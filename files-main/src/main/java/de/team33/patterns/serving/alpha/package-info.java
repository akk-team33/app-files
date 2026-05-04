/**
 * This module/package defines interfaces and provides classes to support the development of event-driven services.
 * <p>
 * The underlying concept is that a service is composed of individual components that can be viewed and used
 * independently at any time, without having to consider the entire service.
 * <p>
 * It should be noted that it is by no means advisable to build every service (exclusively) according to this concept.
 * <p>
 * <b>Basic principles:</b>
 * <p>
 * A service consists of components and possibly sub-services. A component typically carries information,
 * a value of a specific, (ideally) immutable type – hereinafter referred to as its "content".
 * <p>
 * The component is typically mutable, even if this is not necessarily outwardly apparent.
 * That is, its "content" can change or be changed. Furthermore, a component can notify interested parties of changes.
 */
package de.team33.patterns.serving.alpha;