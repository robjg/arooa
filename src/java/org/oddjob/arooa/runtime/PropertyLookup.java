package org.oddjob.arooa.runtime;

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
}
