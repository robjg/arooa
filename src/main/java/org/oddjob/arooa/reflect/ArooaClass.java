package org.oddjob.arooa.reflect;

/**
 * 
 * A container for type information that can be more than just class,
 * for instance when the type is a BeanUtils <code>DynaBean</code>.
 * <p>
 * Implementations should implement equals and hashCode so their
 * {@link BeanOverview}s may be cached. This is because creating them
 * is possibly an expensive operation due to introspection (although this
 * hasn't been proved by the developer).
 * 
 * @author rob
 *
 */
public interface ArooaClass {

    /**
     * Provide the actual Java class this is for.
     *
     * @return A class. Never null.
     */
	Class<?> forClass();

    /**
     * Create a new instance of the Object this represents.
     *
     * @return The object. Never null.
     *
     * @throws ArooaInstantiationException If creation fails.
     */
	Object newInstance() throws ArooaInstantiationException;

    /**
     * Get an overview of the class.
     *
     * @param accessor A property accessor.
     *
     * @return An overview of the class.
     */
	BeanOverview getBeanOverview(PropertyAccessor accessor);
}
