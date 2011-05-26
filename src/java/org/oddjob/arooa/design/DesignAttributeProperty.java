package org.oddjob.arooa.design;

/**
 * Represent the design of a property that is configured as an attribute.
 * 
 * @author rob
 *
 */
public interface DesignAttributeProperty extends DesignProperty {

	/**
	 * Get the attribute text.
	 *  
	 * @return The attribute text. Can be null.
	 */
	public String attribute();
	
	/**
	 * Set the attribute text.
	 * 
	 * @param attribute The attribute. May be null.
	 */
	public void attribute(String attribute);

}
