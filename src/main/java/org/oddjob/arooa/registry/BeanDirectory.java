/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.registry;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;



/**
 * Something that is able to provide components by a path.
 */
public interface BeanDirectory {

	/**
	 * Get the value which is either a bean or the property of a
	 * bean.
	 * <p>
	 * The path can be either:
	 * <ul>
	 *  <li>The id of a bean e.g. <i>mybean</i></li>
	 *  <li>A simple property of a bean e.g. <i>mybean.simple</i></li>
	 *  <li>An indexed property of a bean e.g. <i>mybean.indexed[1]</i></li>
	 *  <li>A mapped property of a bean e.g. <i>mybean.mapped(key)</i></li>
	 *  <li>A nested property of a bean e.g. <i>mybean.complex.simple</i></li>
	 *  <li>A nested bean<i>ownerid/mybean</i></li>
	 *  <li>Most combinations of the above.</li>
	 * </ul>
	 * 
	 * @param path The path.
	 * 
	 * @return The resultant value or null.
	 * 
	 * @throws ArooaPropertyException If property access fails.
	 */
	Object lookup(String path) throws ArooaPropertyException;
	
	/**
	 * Get a value, as above, but also convert it into to given
	 * type.
	 * <p>
	 * Conversion in the directory is required when the client code
	 * has no access to the converters required, for instance in
	 * a nested Oddjob.
	 * 
	 * @param <T> The required type.
	 * @param path The full path
	 * @param required The required type.
	 * 
	 * @return An object of the required type or null if none can
	 * be found.
	 * 
	 * @throws ArooaConversionException If an object can be found but
	 * it can't be converted into the required type.
	 * @throws ArooaPropertyException If property access fails.
	 */
	<T> T lookup(String path,
			Class<T> required)
	throws ArooaPropertyException, ArooaConversionException;
	
	/**
	 * Find the id for the given component.
	 * 
	 * @param bean The component.
	 * @return The id or null if none can be found.
	 */
	String getIdFor(Object bean);
	
	/**
	 * Get all objects in the directory of the required type.
	 * <p>
	 * Why an Iterable not a Collection or Set? It was thought that
	 * this would force read only use. Maybe it should be
	 * an array...
	 * 
	 * @param <T> The required type.
	 * @param type The type.
	 * 
	 * @return An Iterable for matches. Never null.
	 */
	<T> Iterable<T> getAllByType(Class<T> type);
}
