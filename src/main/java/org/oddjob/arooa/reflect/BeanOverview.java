/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.reflect;

/**
 * A Bean Overview is a very light look at the characteristics of
 * a bean.
 * <p>
 *
 */
public interface BeanOverview {

	/**
	 * The names for all the properties.
	 * 
	 * @return An array of names.
	 */
	public String[] getProperties();
	
	/**
	 * Is there a writable property of the given name.
	 * 
	 * @param property The name.
	 * 
	 * @return true/false
	 */
	public boolean hasWriteableProperty(String property);
	
	/**
	 * Is there a readable property of the given name.
	 * 
	 * @param property The name.
	 * 
	 * @return true/false
	 */
	public boolean hasReadableProperty(String property);
	
	/**
	 * Get the property type.
	 * 
	 * @param property The property name.
	 * 
	 * @return The class of the property.
	 * 
	 * @throws ArooaNoPropertyException
	 */
	public Class<?> getPropertyType(String property) throws ArooaNoPropertyException;
	
	/**
	 * Is the property indexed.
	 * 
	 * @param property The property name.
	 * 
	 * @return true/false.
	 * 
	 * @throws ArooaNoPropertyException
	 */
	public boolean isIndexed(String property) throws ArooaNoPropertyException;

	/**
	 * Is the property mapped.
	 * 
	 * @param property The property name.
	 * 
	 * @return true/false.
	 * 
	 * @throws ArooaNoPropertyException
	 */
	public boolean isMapped(String property) throws ArooaNoPropertyException;
	
}
