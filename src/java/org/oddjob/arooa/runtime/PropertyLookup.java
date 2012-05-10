package org.oddjob.arooa.runtime;

import java.util.Set;

/**
 * Something for looking up properties.
 * 
 * @author rob
 *
 */
public interface PropertyLookup {

	/**
	 * Return the value of a property, or null if it doesn't exist.
	 * 
	 * @param propertyName The name of the property.
	 * 
	 * @return The value or null.
	 */
	public String lookup(String propertyName);	
	
	/**
	 * Return a source for the property.
	 * 
	 * @param propertyName The property name.
	 * 
	 * @return The source, or null if the property isn't known.
	 */
	public PropertySource sourceFor(String propertyName);
	
	/**
	 * Return all property names in this property lookup.
	 * 
	 * @return The property names. Never null.
	 */
	public Set<String> propertyNames();
	
	
	
}
