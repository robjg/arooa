/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;

/**
 * Something that can provide conversions between objects of different types.
 * 
 * @author rob
 *
 */
public interface ArooaConverter extends ConversionLookup {

	/**
	 * Convert if possible from the given Object to an equivalent
	 * object of the given type.
	 * 
	 * @param from The object to convert from.
	 * 
	 * @param required The class the object is required to convert to.
	 * 
	 * @return A value of the required class, or null.
	 * 
	 * @throws NoConversionAvailableException If there is no way to convert
	 * the given type to the required type.
	 * 
	 * @throws ConversionFailedException If applying the conversion failed.
	 */
	public <F, T> T convert(F from, Class<T> required)
	throws NoConversionAvailableException, ConversionFailedException;
	
}
