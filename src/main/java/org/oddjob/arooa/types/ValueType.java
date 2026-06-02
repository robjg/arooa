package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.*;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.design.*;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.TextPseudoForm;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;

import java.io.Serial;
import java.io.Serializable;

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
 * <p>
 * {@oddjob.xml.resource org/oddjob/arooa/types/ValueTypeExample1.xml}
 * 
 * @oddjob.example
 *
 * A value that is a reference to a property.
 * <p>
 * {@oddjob.xml.resource org/oddjob/arooa/types/ValueTypeExample2.xml}
 * 
 * @oddjob.example
 * 
 * Examining the internals of a value in Oddjob.
 * <p>
 * {@oddjob.xml.resource org/oddjob/arooa/types/ValueTypeInternalsExample.xml}
 * 
 * The output is:
 * <p>
 * {@oddjob.text.resource org/oddjob/arooa/types/ValueTypeInternalsExampleOut.txt}
 * 
 * 
 * @author Rob Gordon.
 */
public class ValueType implements ArooaValue, Serializable {
	@Serial
    private static final long serialVersionUID = 20070312;
	
	// private static final Logger logger = LoggerFactory.getLogger(ValueType.class);

	public static final ArooaElement ELEMENT = new ArooaElement("value");

	/**
     * @oddjob.property
     * @oddjob.description Any simple value.
     * @oddjob.required No, If missing this value will resolve to be null.
     */
	private ArooaValue value;

	static class ValueTypeJoker implements Joker<ValueType> {

		@Override
		public <T> ConversionStep<ValueType, T> lastStep(ConversionPath<?, ValueType> pathBefore,
														 TypeArooa<ValueType> from,
														 TypeArooa<T> to,
														 ConversionLookup conversions) {

			return new ConversionStep<>() {

                @Override
                public TypeArooa<ValueType> getFromType() {
                    return from;
                }

                @Override
                public TypeArooa<T> getToType() {
                    return to;
                }

                public T convert(ValueType from,
                                 ArooaConverter converter)
                        throws ArooaConversionException {

                    return converter.convert(from.value, to.getType());
                }
            };
		}
	}

	public static class Conversions implements ConversionProvider {
		
		public void registerWith(ConversionRegistry registry) {
			registry.registerJoker(ValueType.class,
					new ValueTypeJoker());
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
