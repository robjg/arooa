package org.oddjob.arooa.registry;

import org.oddjob.arooa.ArooaException;

/**
 * An exception used when creation fails.
 */
public class InvalidIdException extends ArooaException {
	private static final long serialVersionUID = 2009101400L;
	
	private final String id;
	
	public InvalidIdException(String id) {		
		this.id = id;
	}

	public InvalidIdException(String id, String message) {
		super(message);
		this.id = id;
	}
	
	public InvalidIdException(String id, String message, Throwable t) {
		super(message, t);
		this.id = id;
	}
	
	public InvalidIdException(String id, Throwable t) {
		super(t);
		this.id = id;
	}	
	
	public String getId() {
		return id;
	}
}
