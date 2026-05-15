package org.oddjob.arooa.convert;

import java.lang.reflect.Type;

public class EmptyArooaConverter implements ArooaConverter {

	/**
	 * Provides primitive conversions only.
	 */
	private final DefaultConversionRegistry emptyRegistry =
		new DefaultConversionRegistry();
	
    @Override
	public <F, T> T convert(F from, Type required)
	throws NoConversionAvailableException,
	ConversionFailedException {
		if (from == null) {
			return null;
		}
		
		ConversionPath<F, T> path = 
			findConversion(from.getClass(), required);
		
		if (path == null) {
			throw new NoConversionAvailableException(from.getClass(), required);
		}
		
		return path.convert(from, this);
	}

    @Override
    public <F, T> ConversionPath<F, T> findConversion(Type from, Type to) {
        return emptyRegistry.findConversion(from, to);
    }
}
