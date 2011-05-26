package org.oddjob.arooa.reflect;

import org.oddjob.arooa.ArooaConfigurationException;

/**
 * An exception used when creation fails.
 */
public class ArooaPropertyException extends ArooaConfigurationException {
	private static final long serialVersionUID = 20090130L;
	
	private final String property;
	
	public ArooaPropertyException(String property) {		
		super("Property [" + property + "]");
		this.property = property;
	}

	public ArooaPropertyException(String property, String message) {
		super(message);
		this.property = property;
	}
	
	public ArooaPropertyException(String property, String message, Throwable t) {
		super(message, t);
		this.property = property;
	}
	
	public ArooaPropertyException(String property, Throwable t) {
		super(t);
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}
}
