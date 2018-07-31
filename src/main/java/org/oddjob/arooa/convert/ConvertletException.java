/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;

/**
 * Thrown when a {@link Convertlet} can't convert something.
 * 
 * @author rob
 *
 */
public class ConvertletException extends ArooaConversionException {
	private static final long serialVersionUID = 20070202;

	public ConvertletException(String message, Exception cause) {
		super(message, cause);
	}
	
	public ConvertletException(String message) {
		super(message);
	}
	
	public ConvertletException(Exception cause) {
		super(cause);
	}
}
