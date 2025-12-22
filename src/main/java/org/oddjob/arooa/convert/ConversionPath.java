package org.oddjob.arooa.convert;

/**
 * A ConversionPath is a number of ConversionSteps from an Object
 * of one class to an Object of another.
 * 
 * @author rob
 *
 */
public interface ConversionPath<F, T> {

	/**
	 * Create a new ConversionPath by adding the given ConversionStep.
	 * 
	 * @param following The next step.
	 * @return A new path.
	 */
    <X> ConversionPath<F, X> append(ConversionStep<T, X> following);
	
	/**
	 * Create a new ConversionPath by prepending the given ConversionStep.
	 * 
	 * @param preceding The step before.
	 * @return A new path.
	 */
    <X> ConversionPath<X, T> prepend(ConversionStep<X, F> preceding);
	
	/**
	 * Get the 'from' Class of this ConversionPath.
	 * 
	 * @return The 'from' type.
	 */
    default Class<F> getFromClass() {
        return getFromType().getRawType();
    }

    TypeArooa<F> getFromType();

	/**
	 * Get the to Class of this ConversionPath.
	 * 
	 * @return The to type.
	 */
    default Class<T> getToClass() {
        return getToType().getRawType();
    }

    TypeArooa<T> getToType();



	/**
	 * Get the number of Steps in this ConversionPath.
	 * 
	 * @return the number of steps.
	 */
    int length();
	
	/**
	 * Get the conversion step for the given index.
	 * 
	 * @param index The index.
	 * 
	 * @return A ConversionStep.
	 */
    <X, Y> ConversionStep<X, Y> getStep(int index);
	
	/**
	 * Test if this ConversionPath contains a conversion
	 * from the given Class.
	 * 
	 * @param type The type
	 * @return true if it does.
	 */
    boolean contains(TypeArooa<?> type);
	
	/**
	 * Convert the given object with this path.
     *
	 * @param from The thing to convert.
     * @param converter A converter to do the converting.
	 * @return The converted thing.
	 * @throws ConversionFailedException If conversion fails
	 */
	T convert(F from, ArooaConverter converter)
	throws ConversionFailedException;
	
}
