package org.oddjob.arooa.standard;

import org.oddjob.arooa.reflect.ArooaClass;


/**
 * Contains information about the property of a bean.
 * 
 * @author rob
 *
 */
interface PropertyDefinition {
	
	/**
	 * The property name in the parent bean.
	 * 
	 * @return
	 */
	public String getPropertyName();
	
	/**
	 * The class name of the property.
	 * 
	 * @return
	 */
	public ArooaClass getPropertyType();
}
