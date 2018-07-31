/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.reflect;

import java.util.Arrays;

import org.oddjob.arooa.parsing.Location;

/**
 * An exception to be thrown when the property specified does not
 * exist.
 */
public class ArooaNoPropertyException extends ArooaPropertyException {
	private static final long serialVersionUID = 20061018;

	public ArooaNoPropertyException(String property, Class<?> clazz, 
			String[] validProperties) {
		
		this(property, clazz, validProperties, null);
	}	
	
	public ArooaNoPropertyException(String property, Class<?> clazz, 
			String[] validProperties, Throwable e) {
		
		this(property, null, null, clazz, validProperties, e); 
	}
	
	public ArooaNoPropertyException(String property, 
			String message, Throwable e) {
		
		super(property, message, e); 		
	}
	
	public ArooaNoPropertyException(String property, 
			String tag, Location location, 
			Class<?> clazz, String[] validProperties, 
			Throwable e) {
		
		super(property, 
				"There is no property [" + property + "] " +
				(tag == null ? "" : "in element [" + tag + "] ") + 
				(location == null ? "" : "(" + location + ") ") +
				"(" + clazz + ")" +
				(validProperties == null ? "" : ", properties: " + Arrays.toString(validProperties)), 
				e); 		
	}

}

