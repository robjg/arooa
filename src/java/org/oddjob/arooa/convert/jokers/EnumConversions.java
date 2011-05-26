package org.oddjob.arooa.convert.jokers;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.ConversionStep;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Joker;

public class EnumConversions implements ConversionProvider {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void registerWith(ConversionRegistry registry) {
		
		registry.registerJoker(Enum.class, new Joker<Enum>() {
			public <T> ConversionStep<Enum, T> lastStep(
					final Class<? extends Enum> from,
					final Class<T> to, 
					ConversionLookup conversions) {
				
				if (String.class.isAssignableFrom(to)) {
					return new ConversionStep<Enum, T>() {
						public Class<Enum> getFromClass() {
							return Enum.class;
						}
						public Class<T> getToClass() {
							return to;
						}
						public T convert(Enum from,
								ArooaConverter converter)
								throws ArooaConversionException {
							return (T) from.toString();
						}
					};
				}
				return null;
			}
		});
		
		registry.registerJoker(String.class, new Joker<String>() {
			public <T> ConversionStep<String, T> lastStep(
					final Class<? extends String> from,
					final Class<T> to, 
					ConversionLookup conversions) {
				
				if (Enum.class.isAssignableFrom(to)) {
					return new ConversionStep<String, T>() {
						public Class<String> getFromClass() {
							return String.class;
						}
						public Class<T> getToClass() {
							return to;
						}
						public T convert(String from,
								ArooaConverter converter)
								throws ArooaConversionException {
							Class<Enum> enumClass = (Class<Enum>) to;
							return (T) Enum.valueOf(enumClass, from);
						}
					};
				}
				return null;
			}
		});
	}
}
