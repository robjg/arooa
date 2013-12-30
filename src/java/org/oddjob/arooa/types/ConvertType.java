package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.ConversionStep;
import org.oddjob.arooa.convert.Joker;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.life.Configured;
import org.oddjob.arooa.parsing.ArooaElement;

/**
 * @oddjob.description Convert a value to the given Java Class. Most of
 * the time Oddjob's own automatic conversions are fine for setting
 * job properties but occasionally it can be useful to force a conversion
 * to a different type. 
 * <p>
 * This type uses Oddjob's internal converters itself to perform the 
 * conversion. 
 * <p>
 * The <code>is</code> property can provide direct access to the converted
 * value. This can be useful for gaining access to a Java type from Oddjob's
 * wrapper types.
 * 
 * @oddjob.example Convert a delimited list to an array of Strings.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/ConvertDelimitedTextToArray.xml}
 * 
 * The output is:
 * 
 * {@oddjob.text.resource org/oddjob/arooa/types/ConvertDelimitedTextToArray.txt}
 * 
 * @oddjob.example Demonstrate the use of the is property.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/ConvertIsPropertyUsage.xml}
 * 
 * The output is:
 * 
 * {@oddjob.text.resource org/oddjob/arooa/types/ConvertIsPropertyUsage.txt}
 * 
 * 
 * @author rob
 *
 * @param <T>
 */
public class ConvertType<T> implements ArooaValue, ArooaSessionAware {

	public static final ArooaElement ELEMENT = new ArooaElement("convert");
	
	public static class Conversions implements ConversionProvider {
		
		@SuppressWarnings("rawtypes")
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
	 * @oddjob.description The value to convert.
	 * @oddjob.required No. If missing the result of the conversion will be
	 * null.
	 */
	private ArooaValue value;
	
	
	/**
	 * @oddjob.property
	 * @oddjob.description The result of the conversion.
	 * @oddjob.required Read Only.
	 */
	private T is;
	
	/** The session, automatically set. */
	private ArooaSession session;
	
	@Override
	@ArooaHidden
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	@Configured
	public void configured() throws NoConversionAvailableException, ConversionFailedException {
		
		is = convert();

	}
	
	/**
	 * Proivde the conversion.
	 * 
	 * @return
	 * @throws ConversionFailedException 
	 * @throws NoConversionAvailableException 
	 */
	@SuppressWarnings("unchecked")
	public T convert() throws NoConversionAvailableException, ConversionFailedException {
		
		Class<?> to = this.to;
		if (to == null) {
			to = Object.class;
		}

		ArooaConverter converter = session.getTools().getArooaConverter();
		
		return (T) converter.convert(value, to);
	}

	public Class<T> getTo() {
		return to;
	}

	public void setTo(Class<T> to) {
			this.to = to;
	}

	public ArooaValue getValue() {
		return value;
	}

	public void setValue(ArooaValue from) {
		this.value = from;
	}

	public Object getIs() {
		return is;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": [" + value + "] to [" +
				to + "] is [" + is + "]";
	}
}
