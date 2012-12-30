/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.reflect;


/**
 * 
 */
public class PropertySetException extends ArooaPropertyException {
	private static final long serialVersionUID = 20070205;

	private final Class<?> propertyType;
	
	private final Object value;
		
	public PropertySetException(Object bean, String property, 
			Class<?> propertyType, Object value, Throwable cause) {
		super(property, 
			"Failed setting property [" + property + "] of type (" +
			propertyType.getName() + ") in class (" + 
			bean.getClass().getName() + ") with value [" + value + "]" +
			(value == null ? "" : " of type (" + 
					value.getClass().getName() + ")"), 
					cause);
		this.propertyType = propertyType;
		this.value = value;
	}

	public Class<?> getPropertyType() {
		return propertyType;
	}
	
	public Object getValue() {
		return value;
	}
}
