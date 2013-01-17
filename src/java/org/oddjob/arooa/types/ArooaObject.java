package org.oddjob.arooa.types;

import java.io.Serializable;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.ConversionStep;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Joker;

public class ArooaObject implements ArooaValue, Serializable {
	private static final long serialVersionUID = 2009011100L;
	
	private final Object value;

	public static class Conversions implements ConversionProvider {
		
		public void registerWith(ConversionRegistry registry) {
			registry.registerJoker(ArooaObject.class, 
					new Joker<ArooaObject>() {
				public <T> ConversionStep<ArooaObject, T> lastStep(
								Class<? extends ArooaObject> form, 
								final Class<T> to, 
								ConversionLookup conversions) {
					
					return new ConversionStep<ArooaObject, T>() {
						public Class<ArooaObject> getFromClass() {
							return ArooaObject.class;
						}
						public Class<T> getToClass() {
							return to;
						}
						public T convert(ArooaObject from, ArooaConverter converter) 
						throws ArooaConversionException {
							try {
								return converter.convert(from.value, to);
							} catch (Exception e) {
								throw new ArooaConversionException(e);
							}
						}
					};			
				}
			});
		}
	}
	
	public ArooaObject(Object value) {
		this.value = value;
	}

	public Object toValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ArooaObject)) {
			return false;
		}
		
		ArooaObject other = (ArooaObject) obj;
		
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
