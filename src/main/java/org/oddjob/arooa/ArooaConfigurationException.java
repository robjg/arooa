package org.oddjob.arooa;

/**
 * An exception used configuration fails.
 */
public class ArooaConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 2009101400L;
	
	public ArooaConfigurationException() {		
	}

	public ArooaConfigurationException(String message) {
		super(message);
	}
	
	public ArooaConfigurationException(String message, Throwable t) {
		super(message, t);
	}
	
	public ArooaConfigurationException(Throwable t) {
		super(t);
	}	
}
