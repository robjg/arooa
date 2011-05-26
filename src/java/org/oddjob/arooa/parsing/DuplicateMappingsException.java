package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaException;

/**
 * An exception used when creation fails.
 */
public class DuplicateMappingsException extends ArooaException {
	private static final long serialVersionUID = 2009101400L;
	
	public DuplicateMappingsException() {		
	}

	public DuplicateMappingsException(String message) {
		super(message);
	}
	
	public DuplicateMappingsException(String message, Throwable t) {
		super(message, t);
	}
	
	public DuplicateMappingsException(Throwable t) {
		super(t);
	}	
}
