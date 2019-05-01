package org.oddjob.arooa.runtime;

import java.util.Set;

/**
 * Something for looking up properties.
 * 
 * @author rob
 *
 */
public interface PropertyLookup {

	PropertySource SYSTEM_PROPERTY_SOURCE =
			new PropertySource() {
		public String toString() {
			return "SYSTEM";
		}
	};
	
	/**
	 * Return the value of a property, or null if it doesn't exist.
	 * 
	 * @param propertyName The name of the property.
	 * 
	 * @return The value or null.
	 */
	String lookup(String propertyName);
	
	/**
	 * Return a source for the property.
	 * 
	 * @param propertyName The property name.
	 * 
	 * @return The source, or null if the property isn't known.
	 */
	PropertySource sourceFor(String propertyName);
	
	/**
	 * Return all property names in this property lookup.
	 * 
	 * @return The property names. Never null.
	 */
	Set<String> propertyNames();
	
	
	
}
