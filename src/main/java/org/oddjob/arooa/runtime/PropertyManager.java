package org.oddjob.arooa.runtime;

/**
 * Manages Properties.
 * 
 * @author rob
 *
 */
public interface PropertyManager extends PropertyLookup {

	/**
	 * Add a lookup. Lookups are used in the order they are added.
	 * 
	 * @param propertyLookup
	 */
	void addPropertyLookup(PropertyLookup propertyLookup);
	
	
	/**
	 * Add a property overrides. Overrides are used first.
	 * 
	 * @param propertyLookup
	 */
	void addPropertyOverride(PropertyLookup propertyLookup);
	
	
	/**
	 * Remove a lookup.
	 * 
	 * @param propertyLookup
	 */
	void removePropertyLookup(PropertyLookup propertyLookup);
}
