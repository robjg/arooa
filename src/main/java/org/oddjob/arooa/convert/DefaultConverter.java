/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;


import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * The Default {@link ArooaConverter}. This converter uses
 * a {@link ConversionLookup} to provide the 
 * {@link ConversionPath} for the conversion. If none
 * is provided the {@link DefaultConversionLookup} is used.
 *
 * @author rob
 */
public class DefaultConverter implements ArooaConverter {
	
	/** ConvertletRegistry. */
	private final ConversionLookup convertlets;

	/**
	 * Only Constructor.
	 * 
	 * @param convertlets A ConvertletRegistry.
	 */
	public DefaultConverter(ConversionLookup convertlets) {
		this.convertlets = convertlets;
	}

	public DefaultConverter() {
		this.convertlets = new DefaultConversionLookup();
	}

	public static ArooaConverter from(ConversionProvider... conversionProviders) {
		return from(() -> Arrays.stream(conversionProviders).iterator());
	}

	public static ArooaConverter from(Iterable<ConversionProvider> conversionProviders) {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		conversionProviders.forEach(provider -> provider.registerWith(registry));
		return new DefaultConverter(registry.get());
	}

	public ConversionLookup getRegistry() {
		return convertlets;
	}
	
	
	/**
	 * Perform a conversion. This is the main entry point method. It does the
	 * following.
	 * <ul>
	 *  <li>If from is null, return null.
	 * 	<li>If from is an {@link org.oddjob.arooa.ArooaValue} and it's not already
	 * the required ArooaValue then perform a conversion using the best conversion
	 * found from the provided {@link ConversionRegistry}. </li>
	 *  <li>Attempt to find a conversion using the {@link ConversionRegistry}.</li>
	 *  <li>If this fails, and from and required are both arrays. Attempt
	 *  to convert their contents.
	 * </ul>
	 * 
	 * @param from The Object to convert. May be null.
	 * @param required The Class the object is to converted into.
	 * 
	 * @return The converted object. Will be of the class given as
	 * the required parameter or null.
	 * 
	 * @throws NoConversionAvailableException If ther is no conversion to
	 * the required type.
	 * 
	 * @see org.oddjob.arooa.convert.ArooaConverter#convert(Object, Type)
	 */
    @Override
	public <F, T> T convert(F from, Type required)
	throws NoConversionAvailableException, ConversionFailedException {
		if (required == null) {
			throw new NullPointerException("Required class must not be null");
		}
		
		if (from == null) {
			return NullConversions.nullConversionFor(required);
		}

		ConversionPath<F, T> conversionPath = findConversion(from.getClass(), required);
		
		if (conversionPath !=  null) {
			T conversion = conversionPath.convert(from, this);
			if (conversion == null) {
				return NullConversions.nullConversionFor(required);
			}
			else {
				return conversion;
			}
		}
		else {
			throw new NoConversionAvailableException(
				from.getClass(), required);
		}
	}	

	/**
	 * Find a conversion in the convertletRegistry.
	 * 
	 * @param fromClass Class to convert from.
	 * @param required Class to convert to.
	 * 
	 * @return A conversion path that can be used to perform the conversion.
	 */
	public <F, T> ConversionPath<F, T> findConversion(Class<F> fromClass, Class<T> required) {
		
		return convertlets.findConversion(fromClass, required);
	}

    @Override
    public <F, T> ConversionPath<F, T> findConversion(Type from, Type to) {
        return convertlets.findConversion(from, to);
    }
}


