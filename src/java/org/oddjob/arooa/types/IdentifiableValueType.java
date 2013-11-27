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
import org.oddjob.arooa.life.ArooaLifeAware;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.parsing.ArooaElement;

/**
 * @oddjob.description Register the a value with an Id.
 * <p>
 * Unlike components, values can't have an Id. This type allows
 * values to be registered so they can
 * be referenced via the given Id elsewhere in the configuration.
 * <p>
 * Components are registered when the configuration is parsed
 * but the given value will only be registered during the configuration
 * phase, such as when a job runs in Oddjob.
 * <p>
 * 
 * @oddjob.example Register a value.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/IdentifiableValueTypeExample.xml}
 * 
 * @author rob
 *
 */
public class IdentifiableValueType 
implements ArooaValue, ArooaSessionAware, ArooaLifeAware {

	public static final ArooaElement ELEMENT = new ArooaElement("identify");
	
	public static class Conversions implements ConversionProvider {
		
		public void registerWith(ConversionRegistry registry) {
			registry.registerJoker(IdentifiableValueType.class, 
					new Joker<IdentifiableValueType>() {
				public <T> ConversionStep<IdentifiableValueType, T> lastStep(
								Class<? extends IdentifiableValueType> form, 
								final Class<T> to, 
								ConversionLookup conversions) {
					
					return new ConversionStep<IdentifiableValueType, T>() {
						public Class<IdentifiableValueType> getFromClass() {
							return IdentifiableValueType.class;
						}
						public Class<T> getToClass() {
							return to;
						}
						public T convert(IdentifiableValueType from, ArooaConverter converter) 
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
	
	/**
	 * @oddjob.property
	 * @oddjob.description The id to register the value with.
	 * @oddjob.required Yes.
	 */
	private String id;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The value to register.
	 * @oddjob.required No but pointless if missing.
	 */
	private ArooaValue value;
	
	private ArooaSession session;
	
	@Override
	@ArooaHidden
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArooaValue getValue() {
		return value;
	}

	public void setValue(ArooaValue value) {
		this.value = value;
	}
	
	@Override
	public void initialised() {
	}
	
	@Override
	public void configured() {
		if (id == null) {
			throw new IllegalStateException("No Id provided.");
		}
						
		if (value != null) {
			
			Object toRegister;
			// ArooaObject must only have been as the result of
			// a conversion from a basic bean.
			if (value instanceof ArooaObject) {
				toRegister = ((ArooaObject) value).getValue();
			}
			else {
				toRegister = value;
			}
				
			session.getBeanRegistry().register(id, toRegister);
		}
	}
	
	@Override
	public void destroy() {
		if (value != null) {
			session.getBeanRegistry().remove(value);
		}
	}	
}
