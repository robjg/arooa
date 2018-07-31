/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.parsing;

/**
 * Encapsulate the attributes of an element.
 * 
 * @see ArooaElement
 * 
 * @author rob
 *
 */
public interface ArooaAttributes {

	/**
	 * The attribute value.
	 * 
	 * @param name The name of the attribute.
	 * 
	 * @return The value, or null if it doesn't exist.
	 */
	public String get(String name);
	
	/**
	 * Get all the names of the attributes.
	 * 
	 * @return The attribute names. Never null.
	 */
	public String[] getAttributNames();
	
}
