package org.oddjob.arooa.life;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.parsing.ArooaElement;

/**
 * An exception used when creation fails.
 */
public class ArooaElementException extends ArooaConfigurationException {
	private static final long serialVersionUID = 20100512L;
	
	private final ArooaElement element;
	
	public ArooaElementException(ArooaElement element) {		
		super(getMessagePrefix(element).trim());
		this.element = element;
	}

	public ArooaElementException(ArooaElement element, String message) {
		super(getMessagePrefix(element) + message);
		this.element = element;
	}
	
	public ArooaElementException(ArooaElement element, String message, Throwable t) {
		super(getMessagePrefix(element) + message, t);
		this.element = element;
	}
	
	public ArooaElementException(ArooaElement element, Throwable t) {
		super(getMessagePrefix(element).trim(), t);
		this.element = element;
	}
	
	private static String getMessagePrefix(ArooaElement element) {
		return "Element [" + element + "] ";
	}
	
	public ArooaElement getElement() {
		return element;
	}
}
