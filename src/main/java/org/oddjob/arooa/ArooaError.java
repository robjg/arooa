package org.oddjob.arooa;

/**
 * An Error used to wrap a JVM error to add more information.
 */
public class ArooaError extends Error {
	private static final long serialVersionUID = 2018081300L;
	
	public ArooaError() {		
	}

	public ArooaError(String message) {
		super(message);
	}
	
	public ArooaError(String message, Throwable t) {
		super(message, t);
	}
	
	public ArooaError(Throwable t) {
		super(t);
	}	
}
