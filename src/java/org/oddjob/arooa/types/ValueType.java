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
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignProperty;
import org.oddjob.arooa.design.DesignValueBase;
import org.oddjob.arooa.design.SimpleTextAttribute;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.TextPseudoForm;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;

/**
 * @oddjob.description A simple value. This is the most commonly used
 * type. 
 * <p>
 * A value can be:
 * <ul>
 * <li>Any simple type, either text or a number or boolean.</li>
 * <li>It can also be a reference to any other type somewhere else. i.e.
 * value can contain a ${someid.anyvalue} reference.</li>
 * </ul>
 * <p>
 * The ValueType value is expected to be an {@link ArooaValue}. This ensures
 * that references to other types aren't converted until the ValueType 
 * itself is converted. Because of this, simple values will be wrapped
 * as an {@link ArooaObject} by the automatic internal conversion. For
 * normal use this is entirely transparent. An example below demonstrates
 * this.
 * 
 * @oddjob.example
 *
 * A value that is a constant string value.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/ValueTypeExample1.xml}
 * 
 * @oddjob.example
 *
 * A value that is a reference to a property.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/ValueTypeExample2.xml}
 * 
 * @oddjob.example
 * 
 * Examining the internals of a value in Oddjob.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/ValueTypeInternalsExample.xml}
 * 
 * The output is:
 * 
 * {@oddjob.text.resource org/oddjob/arooa/types/ValueTypeInternalsExampleOut.txt}
 * 
 * 
 * @author Rob Gordon.
 */
public class ValueType implements ArooaValue, Serializable {
	private static final long serialVersionUID = 20070312;
	
	// private static final Logger logger = LoggerFactory.getLogger(ValueType.class);

	public static final ArooaElement ELEMENT = new ArooaElement("value");

	/**
     * @oddjob.property
     * @oddjob.description Any simple value.
     * @oddjob.required No, If missing this value will resolve to be null.
     */
	private ArooaValue value;

	public static class Conversions implements ConversionProvider {
		
		public void registerWith(ConversionRegistry registry) {
			registry.registerJoker(ValueType.class,
					new Joker<ValueType>() {
				public <T> ConversionStep<ValueType, T> lastStep(
								Class<? extends ValueType> from, 
								final Class<T> to, 
								ConversionLookup conversions) {
					
					return new ConversionStep<ValueType, T>() {
						
						public Class<ValueType> getFromClass() {
							return ValueType.class;
						}
						
						public Class<T> getToClass() {
							return to;
						}
						
						public T convert(ValueType from,
								ArooaConverter converter)
								throws ArooaConversionException {
							return converter.convert(from.value, to);
						}
					};
				}
			});
		}
	}
		
	public static class ValueDesignFactory implements DesignFactory {

		public DesignInstance createDesign(
				ArooaElement element, 
				ArooaContext arooaContext) {

			return new ValueTypeDesign(element, arooaContext);		
		}
	}
	
	static class ValueTypeDesign extends DesignValueBase {
		
		private final SimpleTextAttribute value;

		ValueTypeDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, parentContext);

			value = new SimpleTextAttribute("value", this);
		}

		public DesignProperty[] children() {
			return new DesignProperty[] { value };
		}

		public Form detail() {
			return new TextPseudoForm(value);
		}

	}	

	/** 
	 * @oddjob.property value
	 * @oddjob.description The value.
	 * @oddjob.required No.
	 */
	@ArooaAttribute
	public void setValue(ArooaValue value) {
		this.value = value;
	}

	public ArooaValue getValue() {
		return value;
	}
	
	public String toString() {
		if (value == null) {
			return "null";
		}
		return value.toString();
	}

}
