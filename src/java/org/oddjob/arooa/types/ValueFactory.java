package org.oddjob.arooa.types;

import java.lang.reflect.Method;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.ConversionStep;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Joker;

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
					
					Method m;
					try {
						m = from.getMethod("toValue");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					Class returnType = m.getReturnType();
			    	if (to.isAssignableFrom(returnType)) {
						return new ConversionStep<ValueFactory, T>() {
							public Class<ValueFactory> getFromClass() {
								return ValueFactory.class;
							}
							public Class<T> getToClass() {
								return to;
							}
							public T convert(ValueFactory from, ArooaConverter converter) 
							throws ArooaConversionException {
									return (T) from.toValue();
							}
						};
			    	}
			    	return null;
				}
					
			});
		}
	}
	
	public T toValue() throws ArooaConversionException;
}
