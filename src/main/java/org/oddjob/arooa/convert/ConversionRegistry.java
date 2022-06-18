package org.oddjob.arooa.convert;

/**
 * Something that is able to use a set of {@link Convertlet}s to find
 * a {@link ConversionPath} between two classes.
 * 
 * @author Rob Gordon.
 *
 */
public interface ConversionRegistry {

	/**
	 * Register a Convertlet.
	 * 
	 * @param from The convert from class.
	 * @param to The convert to class.
	 * @param convertlet The Convertlet.
	 */
	<F, T> void register(Class<F> from, Class<T> to,
			Convertlet<F, T> convertlet);
	
	
	/**
	 * Register a joker which is a conversion that trumps all other
	 * conversion. The joker must be able to perform the final conversion
	 * step or throw a {@link NoConversionAvailableException}.
	 * 
	 * @param <F> The class of the from type.
	 * 
	 * @param from The from type.
	 * @param joker The joker.
	 */
	<F> void registerJoker(Class<F> from, Joker<F> joker);
}
