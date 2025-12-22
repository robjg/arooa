package org.oddjob.arooa.convert;

import java.io.Serial;

/**
 * Caused when a Conversion fails.
 * 
 * @author rob
 *
 */
public class ConversionFailedException extends ArooaConversionException {
	@Serial
    private static final long serialVersionUID = 20070328;
	
	private final ConversionStack conversionStack;
	
	public ConversionFailedException(ConversionStack conversionStack, 
			Exception cause) {
		super("Conversion failed between [" +
				conversionStack.getConversionPath().getFromClass() + "] and [" +
				conversionStack.getConversionPath().getToClass() + "]",
				cause);
		this.conversionStack = conversionStack;
	}
	
	public ConversionStack getConversionStack() {
		return conversionStack;
	}
	
}
