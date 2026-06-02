package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Provide a conversion from any Java Object to an {@link ArooaValue}.
 * 
 * @see ValueType
 * 
 * @author rob
 *
 */
public class ArooaObject implements ArooaValue, Serializable {
	@Serial
    private static final long serialVersionUID = 2009011100L;
	
	private final Object value;

	/**
	 * @oddjob.conversion Wrap any Object so it can be an {@code ArooaValue}. Required so any
	 * bean can be used as an Oddjob Variable.
	 */
	static class ArooaObjectJoker implements Joker<ArooaObject> {

		@Override
		public <T> ConversionStep<ArooaObject, T> lastStep(ConversionPath<?, ArooaObject> pathBefore,
														   TypeArooa<ArooaObject> from,
														   TypeArooa<T> to,
														   ConversionLookup conversions) {

			return new ConversionStep<>() {

				@Override
				public TypeArooa<ArooaObject> getFromType() {
					return from;
				}

				@Override
				public TypeArooa<T> getToType() {
					return to;
				}

				public T convert(ArooaObject from, ArooaConverter converter)
						throws ArooaConversionException {
					try {
						return converter.convert(from.value, to.getType());
					} catch (Exception e) {
						throw new ArooaConversionException(e);
					}
				}
			};
		}
	}

	public static class Conversions implements ConversionProvider {
		
		public void registerWith(ConversionRegistry registry) {
			registry.registerJoker(ArooaObject.class,
					new ArooaObjectJoker());
		}
	}
	
	public ArooaObject(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ArooaObject other)) {
			return false;
		}

        if (this.value == null) {
			return other.value == null;
		}
		
		return this.value.equals(other.value);
	}

	@Override
	public int hashCode() {
		if (value == null) {
			return 0;
		}
		return value.hashCode();
	}
	
	@Override
	public String toString() {
		if (value == null) {
			return "null";
		}
		return value.toString();
	}
}
