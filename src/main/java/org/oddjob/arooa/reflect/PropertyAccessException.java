/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.reflect;


/**
 * 
 */
public class PropertyAccessException extends ArooaPropertyException {
	private static final long serialVersionUID = 20070205;
	
	public PropertyAccessException(Object bean, String property, String message) {
		super(property, "Failed accessing property [" + property + "]" +
				(bean == null ? ""  : " (" + bean.getClass().getName() + ") : ") + 
						message);
	}
	
	public PropertyAccessException(Object bean, String property, Throwable cause) {
		super(property, "Failed accessing property [" + property + "]" +
				(bean == null ? ""  : " (" + bean.getClass().getName() + ")"), 
						cause);
	}


	public PropertyAccessException(String property, Throwable cause) {
		this(null, property, cause);
	}
}
