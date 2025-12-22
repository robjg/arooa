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
	Class<F> getFromClass();

    default TypeArooa<F> getFromType() {
        return TypeArooa.of(getFromClass());
    }

	/**
	 * Get the class the ConversionStep is to.
	 * 
	 * @return The class. Never null.
	 */
	Class<T> getToClass();

    default TypeArooa<T> getToType() {
        return TypeArooa.of(getToClass());
    }

	T convert(F from, ArooaConverter converter)
	throws ArooaConversionException;
	
}