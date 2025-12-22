package org.oddjob.arooa.convert;

import java.lang.reflect.Type;

/**
 * Provide a {@link ConversionLookup} for the default conversions
 * as given by {@link DefaultConversionProvider}.
 * 
 * @author rob
 *
 */
public class DefaultConversionLookup implements ConversionLookup {

	private final DefaultConversionRegistry registry = 
		new DefaultConversionRegistry();
	
	public DefaultConversionLookup() {
		new DefaultConversionProvider().registerWith(registry);
	}
	
	
	public <F, T> ConversionPath<F, T> findConversion(Class<F> from, Class<T> to) {
		return registry.findConversion(from, to);
	}

    @Override
    public <F, T> ConversionPath<F, T> findConversion(TypeArooa<F> from, Type to) {
        return registry.findConversion(from, to);
    }
}
