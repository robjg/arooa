package org.oddjob.arooa;

/**
 * An exception used when creation fails.
 */
public class ArooaException extends RuntimeException {
	private static final long serialVersionUID = 20090130L;
	
	public ArooaException() {		
	}

	public ArooaException(String message) {
		super(message);
	}
	
	public ArooaException(String message, Throwable t) {
		super(message, t);
	}
	
	public ArooaException(Throwable t) {
		super(t);
	}	
}
