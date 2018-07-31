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
	public void addPropertyLookup(PropertyLookup propertyLookup);
	
	
	/**
	 * Add a property overrides. Overrides are used first.
	 * 
	 * @param propertyLookup
	 */	
	public void addPropertyOverride(PropertyLookup propertyLookup);
	
	
	/**
	 * Remove a lookup.
	 * 
	 * @param propertyLookup
	 */
	public void removePropertyLookup(PropertyLookup propertyLookup);
}
