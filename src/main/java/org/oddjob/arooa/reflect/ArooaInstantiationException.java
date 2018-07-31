package org.oddjob.arooa.reflect;

import org.oddjob.arooa.ArooaException;

/**
 * Thrown when something can't be created.
 * 
 * @author rob
 *
 */
public class ArooaInstantiationException extends ArooaException {

	private static final long serialVersionUID = 2010012600L;
	
	public ArooaInstantiationException() {		
	}

	public ArooaInstantiationException(String message) {
		super(message);
	}
	
	public ArooaInstantiationException(String message, Throwable t) {
		super(message, t);
	}
	
	public ArooaInstantiationException(Throwable t) {
		super(t);
	}	
}
