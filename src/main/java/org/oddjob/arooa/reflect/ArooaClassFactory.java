package org.oddjob.arooa.reflect;

/**
 * Find a {@link ArooaClass} for an Object of the type.
 * @author rob
 *
 * @param <T>
 */
public interface ArooaClassFactory<T> {

	/**
	 * Find the class.
	 * 
	 * @param instance The object of the type the factory supports.
	 * 
	 * @return The {@link ArooaClass}. Must not be null.
	 */
	public ArooaClass classFor(T instance);
}
