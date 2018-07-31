package org.oddjob.arooa.reflect;

/**
 * Provide extra information about a bean. The need for this information came
 * from Java's <code>Class.getMethod()</code> and 
 * <code>BeanUtils.getPropertyDescriptors()</code> not guaranteeing the order 
 * of properties.
 * 
 * @author rob
 *
 */
public interface BeanView {

	/**
	 * Get the names of the readable properties.
	 * 
	 * @return An array. Never null and never containing null elements.
	 */
	public String[] getProperties();
	
	/**
	 * For a given property name, provide the title for the property.
	 * 
	 * @param property The property name. Must not be null.
	 * @return The title. Which may be the same as the property. Never null.
	 */
	public String titleFor(String property);
	
}
