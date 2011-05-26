package org.oddjob.arooa.design.view;

import org.oddjob.arooa.ArooaException;

/**
 * An exception in the view.
 */
public class DesignViewException extends ArooaException {
	private static final long serialVersionUID = 2009101600L;
	
	public DesignViewException() {		
	}

	public DesignViewException(String message) {
		super(message);
	}
	
	public DesignViewException(String message, Throwable t) {
		super(message, t);
	}
	
	public DesignViewException(Throwable t) {
		super(t);
	}	
}
