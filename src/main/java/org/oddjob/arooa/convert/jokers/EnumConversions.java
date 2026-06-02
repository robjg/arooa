package org.oddjob.arooa.convert.jokers;

import org.oddjob.arooa.convert.*;

public class EnumConversions implements ConversionProvider {

	static class FromEnumConvertlet implements Convertlet<Enum<?>, String> {

		@Override
		public String convert(Enum<?> from) throws ArooaConversionException {
			return from.toString();
		}

	}

	static class ToEnumJoker implements  Joker<String> {

		@Override
		public <T> ConversionStep<String, T> lastStep(ConversionPath<?, String> pathBefore,
													  TypeArooa<String> from,
													  TypeArooa<T> to,
													  ConversionLookup conversions) {

			if (Enum.class.isAssignableFrom(to.getRawType())) {
				return new ConversionStep<>() {

                    @Override
                    public TypeArooa<String> getFromType() {
                        return from;
                    }

                    @Override
                    public TypeArooa<T> getToType() {
                        return to;
                    }

                    @Override
                    public T convert(String from,
                                     ArooaConverter converter) {
                        //noinspection rawtypes,unchecked
                        return (T) Enum.valueOf((Class<Enum>) to.getType(), from);
                    }
                };
			}
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void registerWith(ConversionRegistry registry) {
		
		registry.register(Enum.class, String.class,
				(Convertlet<Enum, String>) ((Convertlet) new FromEnumConvertlet()));
		
		registry.registerJoker(String.class, new ToEnumJoker());
	}
}
