/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;


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
	 * found from the provided {@link ArooaConversionKit}. </li>
	 *  <li>Attempt to find a conversion using the conversion registry.</li>
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
	 * @see org.oddjob.arooa.convert.ArooaConverter#convert(java.lang.Object, java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <F, T> T convert(F from, Class<T> required) 
	throws NoConversionAvailableException, ConversionFailedException {
		if (required == null) {
			throw new NullPointerException("Required class must not be null");
		}
		if (from == null) {
			return null;
		}
		
		Class<F> fromClass = (Class<F>) from.getClass();
		
		ConversionPath<F, T> conversionPath = findConversion(fromClass, required);
		
		if (conversionPath !=  null) {
			return conversionPath.convert(from, this);
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
	 * 
	 * @throws NoConversionAvailableException If no conversion is available.
	 */
	public <F, T> ConversionPath<F, T> findConversion(Class<F> fromClass, Class<T> required) {
		
		return convertlets.findConversion(fromClass, required);
	}
	
}


