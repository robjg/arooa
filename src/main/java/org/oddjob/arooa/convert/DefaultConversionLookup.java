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

	private final ConversionLookup conversionLookup;
	
	public DefaultConversionLookup() {

		DefaultConversionRegistry registry =
				new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		conversionLookup = registry.get();
	}

    @Override
    public <F, T> ConversionPath<F, T> findConversion(Type from, Type to) {
        return conversionLookup.findConversion(from, to);
    }
}
