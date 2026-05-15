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

    @Override
    public <F, T> ConversionPath<F, T> findConversion(Type from, Type to) {
        return registry.findConversion(from, to);
    }
}
