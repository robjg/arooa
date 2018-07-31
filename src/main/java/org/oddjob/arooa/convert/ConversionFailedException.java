package org.oddjob.arooa.convert;

/**
 * Caused when a Conversion fails.
 * 
 * @author rob
 *
 */
public class ConversionFailedException extends ArooaConversionException {
	private static final long serialVersionUID = 20070328;
	
	private final ConversionStack conversionStack;
	
	public ConversionFailedException(ConversionStack conversionStack, 
			Exception cause) {
		super("Conversion failed between [" +
				conversionStack.getConversionPath().getFromClass().getName() + "] and [" + 
				conversionStack.getConversionPath().getToClass().getName() + "]", 
				cause);
		this.conversionStack = conversionStack;
	}
	
	public ConversionStack getConversionStack() {
		return conversionStack;
	}
	
}
