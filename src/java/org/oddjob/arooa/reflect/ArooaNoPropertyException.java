/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.reflect;

import org.oddjob.arooa.parsing.Location;

/**
 * 
 */
public class ArooaNoPropertyException extends ArooaPropertyException {
	private static final long serialVersionUID = 20061018;

	public ArooaNoPropertyException(String property, Class<?> clazz) {
		this(property, clazz, null);
	}	
	
	public ArooaNoPropertyException(String property, Class<?> clazz, Throwable e) {
		super(property, "There is no property [" + property + "] " +
				"(" + clazz + ")", e); 
	}
	
	public ArooaNoPropertyException(String property, String tag, Location location, 
			Class<?> clazz, Throwable e) {
		super(property, "There is no property [" + property + "] " +
				(tag == null ? "" : "in element [" + tag + "] ") + 
				(location == null ? "" : "(" + location + ") ") +
				"(" + clazz + ")", e); 
	}

}

