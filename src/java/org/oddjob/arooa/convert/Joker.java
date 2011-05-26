package org.oddjob.arooa.convert;

import org.oddjob.arooa.types.ValueType;


/**
 * A Joker is used by types to offer a final conversion solution that trumps all
 * others. It is generally used by types that could be anything such
 * as {@link ValueType}.
 * 
 * @author rob
 *
 * @param <F> The from type.
 */
public interface Joker<F> {

	/**
	 * Provide the final step. Will return null if there
	 * is no conversion to the required type.
	 * 
	 * @param <T> The to type.
	 * 
	 * @param from The from class.
	 * @param to The to class.
	 * @param conversions The conversions this Jokers is part of. Useful for
	 * converting content type but be careful of recursion.
	 * 
	 * @return The ConversionStep or null.
	 */
	public <T> ConversionStep<F, T> lastStep(
			Class<? extends F> from, Class<T> to, 
			ConversionLookup conversions);
}
