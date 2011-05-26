package org.oddjob.arooa.reflect;

/**
 * 
 * A container for type information that can be more than just class,
 * for instance when the type is a BeanUtils <code>DynaBean</code>.
 * <p>
 * Implementations should implement equals and hashCode so their
 * {@link BeanOverview}s may be cached. This is because creating them
 * is possibly an expensive operation due to introspection (although this
 * hasn't been prooved by the developer).
 * 
 * @author rob
 *
 */
public interface ArooaClass {

	public Class<?> forClass();
	
	public Object newInstance() throws ArooaInstantiationException;
	
	public BeanOverview getBeanOverview(PropertyAccessor accessor);
}
