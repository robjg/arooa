package org.oddjob.arooa.convert;

import java.lang.reflect.Type;

public class EmptyArooaConverter implements ArooaConverter {

	/**
	 * Provides primitive conversions only.
	 */
	private final DefaultConversionRegistry emptyRegistry =
		new DefaultConversionRegistry();
	
	@SuppressWarnings("unchecked")
    @Override
	public <F, T> T convert(F from, Type required)
	throws NoConversionAvailableException,
	ConversionFailedException {
		if (from == null) {
			return null;
		}
		
		ConversionPath<F, T> path = 
			findConversion((TypeArooa<F>) TypeArooa.of(from.getClass()), required);
		
		if (path == null) {
			throw new NoConversionAvailableException(from.getClass(), required);
		}
		
		return path.convert(from, this);
	}

    @Override
    public <F, T> ConversionPath<F, T> findConversion(Class<F> from, Class<T> to) {
        return emptyRegistry.findConversion(from, to);
    }

    @Override
    public <F, T> ConversionPath<F, T> findConversion(TypeArooa<F> from, Type to) {
        return emptyRegistry.findConversion(from, to);
    }
}
