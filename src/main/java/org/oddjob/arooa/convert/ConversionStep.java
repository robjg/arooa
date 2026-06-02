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
	 * Get the type the ConversionStep is from.
	 * 
	 * @return The type. Never null.
	 */
    TypeArooa<F> getFromType();

	/**
	 * Get the type the ConversionStep is to.
	 * 
	 * @return The type. Never null.
	 */
    TypeArooa<T> getToType();

	T convert(F from, ArooaConverter converter)
	throws ArooaConversionException;
	
}