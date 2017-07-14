package org.oddjob.arooa.convert.jokers;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.ConversionStep;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.Joker;

public class ArrayConversions implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.registerJoker(Object.class, new Joker<Object>() {
			
			@SuppressWarnings("unchecked")
			public <T> ConversionStep<Object, T> lastStep(
					Class<? extends Object> from, 
					final Class<T> to, 
					ConversionLookup conversions) {
				
				if (from.isArray() && to.isAssignableFrom(List.class)) {
					
					return new ConversionStep<Object, T>() {
						public Class<Object> getFromClass() {
							return Object.class;
						}
						public Class<T> getToClass() {
							return to;
						}
						public T convert(Object from, ArooaConverter converter)
								throws ArooaConversionException {
							final Object[] array = (Object[]) from;
							return (T) Arrays.asList(array);
						}
					};
				}
				if (from.isArray() && to.isArray()) {
					
					Class<?> fromComponent = from.getComponentType();
					Class<?> toComponent = to.getComponentType();
					
					@SuppressWarnings("rawtypes")
					final ConversionPath componentPath = 
						conversions.findConversion(fromComponent, toComponent);
					
					if (componentPath == null) {
						return null;
					}
						
					return new ConversionStep<Object, T>() {
						public Class<Object> getFromClass() {
							return Object.class;
						}
						public Class<T> getToClass() {
							return to;
						}
						public T convert(Object from, ArooaConverter converter)
								throws ArooaConversionException {
							final Object[] array = (Object[]) from;
							Object newArray = Array.newInstance(
									to.getComponentType(), array.length);
							for (int i = 0; i < array.length; ++i) {
								Object convertedElement = null;
								try { 
									convertedElement = componentPath.convert(array[i], converter);
								} catch (ArooaConversionException e) {
									throw new ConvertletException(e);
								}
								if (convertedElement != null) {
									Array.set(newArray, i, 
											convertedElement);
								}
							}
							
							return (T) newArray;
						}
					};
				}
				if (from.isArray() && to.isAssignableFrom(String.class)) {
					
					return (ConversionStep<Object, T>) toStringConversion(from, conversions);
				}
				if (to.isArray()) {
					
					Class<?> toComponent = to.getComponentType();
					
					@SuppressWarnings("rawtypes")
					final ConversionPath componentPath = 
						conversions.findConversion(from , toComponent);
					
					if (componentPath == null) {
						return null;
					}
					
					return new ConversionStep<Object, T>() {
						public Class<Object> getFromClass() {
							return Object.class;
						}
						public Class<T> getToClass() {
							return to;
						}
						public T convert(Object from, ArooaConverter converter)
								throws ArooaConversionException {
							Object newArray = Array.newInstance(
									to.getComponentType(), 1);
							
							Object convertedElement = null;
							try { 
								convertedElement = componentPath.convert(from, converter);
							} catch (ArooaConversionException e) {
								throw new ConvertletException(e);
							}
							if (convertedElement != null) {
								Array.set(newArray, 0, 
										convertedElement);
							}
							
							return (T) newArray;
						}
					};
				}
				return null;
			}
		});
	}
	
	static <T> ConversionStep<? extends Object, String> toStringConversion(
			final Class<? extends Object> fromType, ConversionLookup conversions) {
		
		@SuppressWarnings("unchecked")
		Class<T> fromComponent = (Class<T>) fromType.getComponentType();

		final ConversionPath<T, String> componentConversion = 
				conversions.findConversion(fromComponent, String.class);

		return new ConversionStep<Object, String>() {

			@Override
			public Class<Object> getFromClass() {
				return Object.class;
			}

			@Override
			public Class<String> getToClass() {
				return String.class;
			}
			
			@Override
			public String convert(Object from, ArooaConverter converter) throws ArooaConversionException {
				
				Object[] fromArray = (Object[]) from;
				
				StringBuilder builder = new StringBuilder();
				for (Object element : fromArray) {
					if (builder.length() > 0) {
						builder.append(", ");
					}
					
					@SuppressWarnings("unchecked")
					String elementStr = componentConversion.convert((T) element, converter);
					builder.append(String.valueOf(elementStr));
				}
				return builder.toString();
			}
		};
	}
	
}
