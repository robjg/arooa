/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;

/**
 * A <code>Convertlet</code> provides a single conversion from an object of a 
 * certain type to an object of a different type.
 * <p>
 * <code>Convertlet</code>s are provided by {@link ConversionProvider} in an 
 * {@link ArooaDescriptor}.
 * <p>
 * <code>Convertlet</code>s are used in sequences. A conversion from Float to Integer 
 * uses to <code>Convertlet</code>s. Float to Number and Number to Integer. If
 * a convertlet isn't to be used as part of a sequence it should be 
 * declared as a {@link FinalConvertlet}.
 * <p>
 * A Convertlet can always assume that they will
 * not be used for an inappropriate conversion and so do not have to do
 * any type checking.
 * 
 * @author rob
 *
 */
@FunctionalInterface
public interface Convertlet<F, T> {
	
	/**
	 * Convert from one thing to another.
	 * 
	 * @param from The from object, never null.
	 * @return The conversion. Can be null.
	 * 
	 * @throws ArooaConversionException If conversion failed - for instance
	 * a number or date can't be parsed.
	 */
	T convert(F from) throws ArooaConversionException;
}
