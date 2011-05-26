/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;

/**
 * A ConversionStep is one step in a {@link ConversionPath}.
 * 
 * @author Rob Gordon.
 *
 */
public interface ConversionStep<F, T> {
	
	/**
	 * Get the class the ConversionStep is from.
	 * 
	 * @return The class. Never null.
	 */
	public Class<F> getFromClass();

	/**
	 * Get the class the ConversionStep is to.
	 * 
	 * @return The class. Never null.
	 */
	public Class<T> getToClass();
	
	public T convert(F from, ArooaConverter converter)
	throws ArooaConversionException;
	
}