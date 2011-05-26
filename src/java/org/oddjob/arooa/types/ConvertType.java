package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.ConversionStep;
import org.oddjob.arooa.convert.Joker;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.parsing.ArooaElement;

/**
 * @oddjob.description Convert a value to the given Java Class. 
 * 
 * @oddjob.example Convert a delimited list to an array of Strings.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/ConvertDelimitedTextToArray.xml}
 * 
 * @author rob
 *
 * @param <T>
 */
public class ConvertType<T> implements ArooaValue, ArooaSessionAware {

	public static final ArooaElement ELEMENT = new ArooaElement("convert");
	
	public static class Conversions implements ConversionProvider {
		
		@SuppressWarnings({ "unchecked" })
		public void registerWith(ConversionRegistry registry) {
			registry.registerJoker(ConvertType.class, 
					new Joker<ConvertType>() {
				public <T> ConversionStep<ConvertType, T> lastStep(
						Class<? extends ConvertType> from, 
						final Class<T> to, 
						ConversionLookup conversions) {
						
					return new ConversionStep<ConvertType, T>() {
						public Class<ConvertType> getFromClass() {
							return ConvertType.class;
						}
						public Class<T> getToClass() {
							return to;
						}
						public T convert(ConvertType from, ArooaConverter converter) 
						throws ArooaConversionException {
							Object converted = from.convert();
							
							return converter.convert(converted, to);
						}
					};
				}
			});
		}
	}
	
	/**
	 * @oddjob.property
	 * @oddjob.description The name of the java class to convert to.
	 * @oddjob.required Yes.
	 */
	private Class<T> to;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The value to conert.
	 * @oddjob.required No. If missing the result of the conversion will be
	 * null.
	 */
	private Object value;
	
	/** The session, automatically set. */
	private ArooaSession session;
	
	@Override
	@ArooaHidden
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	public T convert() throws ArooaConversionException {
		
		ArooaConverter converter = session.getTools().getArooaConverter();
		
		return converter.convert(value, to);
	}

	public Class<T> getTo() {
		return to;
	}

	public void setTo(Class<T> to) {
			this.to = to;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object from) {
		this.value = from;
	}
}
