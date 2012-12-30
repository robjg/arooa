package org.oddjob.arooa.reflect;

import org.oddjob.arooa.ArooaConfigurationException;

/**
 * An exception used when creation fails.
 */
public class ArooaPropertyException extends ArooaConfigurationException {
	private static final long serialVersionUID = 20090130L;
	
	private final String property;
	
	public ArooaPropertyException(String property) {		
		this(property, null, null);
	}

	public ArooaPropertyException(String property,  Throwable t) {
		this(property, null, t);
	}
	
	public ArooaPropertyException(String property, String message) {
		this(property, message, null);
	}
	
	public ArooaPropertyException(String property, String message, 
			Throwable t) {
		super(message == null ?
				"Property [" + property + "]" : 
					message, t);
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}	
}
