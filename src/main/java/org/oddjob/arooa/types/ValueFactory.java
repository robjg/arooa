package org.oddjob.arooa.types;

import java.lang.reflect.Method;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.ConversionStep;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Joker;


/**
 * An interface for simple {@link ArooaValue}s that only resolve to a
 * thing of a single type. 
 * <p>
 * Implementing this interface saves the new Type the trouble of needing 
 * to register a conversion.
 * 
 * @author rob
 *
 * @param <T> The type that the value will resolve to.
 */
public interface ValueFactory<T> extends ArooaValue {

	public static class Conversions implements ConversionProvider {
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void registerWith(ConversionRegistry registry) {
			registry.registerJoker(ValueFactory.class, 
					new Joker<ValueFactory>() {
				public <T> ConversionStep<ValueFactory, T> lastStep(
						final Class <? extends ValueFactory> from, 
						final Class<T> to, 
						ConversionLookup conversions) {
					
					// Get the return type.
					Method m;
					try {
						m = from.getMethod("toValue");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					Class returnType = m.getReturnType();
					
					// Is there a conversion path from the type of the
					// factory to the required to type.
					final ConversionPath finalConversion = 
							conversions.findConversion(returnType, to);
					if (finalConversion == null) {
						return null;
					}
					
					return new ConversionStep<ValueFactory, T>() {
						public Class<ValueFactory> getFromClass() {
							return ValueFactory.class;
						}
						public Class<T> getToClass() {
							return to;
						}
						public T convert(ValueFactory from, ArooaConverter converter) 
						throws ArooaConversionException {
								return (T) finalConversion.convert(
										from.toValue(), converter);
						}
					};
				}
					
			});
		}
	}
	
	/**
	 * Provide an instance of the type this is a factory for.
	 * 
	 * @return An instance of the type. May be null.
	 * 
	 * @throws ArooaConversionException If unable to provide a value.
	 */
	public T toValue() throws ArooaConversionException;
	
}
