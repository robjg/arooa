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
	 * @param following
	 * @return
	 */
	public <X> ConversionPath<F, X> append(ConversionStep<T, X> following);
	
	/**
	 * Create a new ConversionPath by prepending the given ConversionStep.
	 * 
	 * @param preceeding
	 * @return
	 */
	public <X> ConversionPath<X, T> prepend(ConversionStep<X, F> preceeding);
	
	/**
	 * Get the from Class of this ConversionPath.
	 * 
	 * @return
	 */
	public Class<F> getFromClass();
	
	/**
	 * Get the to Class of this ConversionPath.
	 * 
	 * @return
	 */
	public Class<T> getToClass();

	/**
	 * Get the number of Steps in this ConversionPath.
	 * 
	 * @return
	 */
	public int length();
	
	/**
	 * Get the conversion step for the given index.
	 * 
	 * @param index The index.
	 * 
	 * @return A ConversionStep.
	 */
	public <X, Y> ConversionStep<X, Y> getStep(int index);
	
	/**
	 * Test if this ConversionPath contains a conversion
	 * from the given Class.
	 * 
	 * @param from
	 * @return
	 */
	public boolean contains(Class<?> from);
	
	/**
	 * 
	 * @param from
	 * @return
	 * @throws ConversionFailedException
	 */
	public T convert(F from, ArooaConverter converter)
	throws ConversionFailedException;
	
}
