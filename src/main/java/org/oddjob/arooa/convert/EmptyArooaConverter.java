package org.oddjob.arooa.convert;

public class EmptyArooaConverter implements ArooaConverter {

	/**
	 * Provides primitive conversions only.
	 */
	private final DefaultConversionRegistry emptyRegistry =
		new DefaultConversionRegistry();
	
	@SuppressWarnings("unchecked")
	public <F, T> T convert(F from, Class<T> required)
	throws NoConversionAvailableException,
	ConversionFailedException {
		if (from == null) {
			return null;
		}
		
		ConversionPath<F, T> path = 
			findConversion((Class<F>) from.getClass(), required);
		
		if (path == null) {
			throw new NoConversionAvailableException(from.getClass(), required);
		}
		
		return path.convert(from, this);
	}
	
	public <F, T> ConversionPath<F, T> findConversion(
			final Class<F> from, final Class<T> to) {		
		
		return emptyRegistry.findConversion(from, to);
	}
}
