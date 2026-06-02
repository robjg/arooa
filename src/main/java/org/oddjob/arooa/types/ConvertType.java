package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.*;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.life.Configured;
import org.oddjob.arooa.parsing.ArooaElement;

/**
 * @author rob
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
 * @oddjob.example Convert a delimited list to an array of Strings.
 * <p>
 * {@oddjob.xml.resource org/oddjob/arooa/types/ConvertDelimitedTextToArray.xml}
 * <p>
 * The output is:
 * <p>
 * {@oddjob.text.resource org/oddjob/arooa/types/ConvertDelimitedTextToArray.txt}
 * @oddjob.example Demonstrate the use of the is property.
 * <p>
 * {@oddjob.xml.resource org/oddjob/arooa/types/ConvertIsPropertyUsage.xml}
 * <p>
 * The output is:
 * <p>
 * {@oddjob.text.resource org/oddjob/arooa/types/ConvertIsPropertyUsage.txt}
 *
 */
public class ConvertType implements ArooaValue, ArooaSessionAware {

    public static final ArooaElement ELEMENT = new ArooaElement("convert");

    static class ConvertTypeJoker implements Joker<ConvertType> {

        @Override
        public <T> ConversionStep<ConvertType, T> lastStep(ConversionPath<?, ConvertType> pathBefore,
                                                           TypeArooa<ConvertType> from,
                                                           TypeArooa<T> to,
                                                           ConversionLookup conversions) {

            return new ConversionStep<>() {

                public Class<ConvertType> getFromClass() {
                    return from.getRawType();
                }

                @Override
                public TypeArooa<ConvertType> getFromType() {
                    return from;
                }

                public Class<T> getToClass() {
                    return to.getRawType();
                }

                @Override
                public TypeArooa<T> getToType() {
                    return to;
                }

                public T convert(ConvertType from, ArooaConverter converter)
                        throws ArooaConversionException {
                    Object converted = from.convert();

                    return converter.convert(converted, to.getType());
                }
            };
        }
    }

    public static class Conversions implements ConversionProvider {

        public void registerWith(ConversionRegistry registry) {
            registry.registerJoker(ConvertType.class,
                    new ConvertTypeJoker());
        }
    }

    /**
     * @oddjob.property
     * @oddjob.description The name of the java class to convert to.
     * @oddjob.required Yes.
     */
    private Class<?> to;

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
    private Object is;

    /**
     * The session, automatically set.
     */
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
     * Provide the conversion.
     *
     * @return The result of the conversion
     * @throws ConversionFailedException      If it fails.
     * @throws NoConversionAvailableException If there's no conversion available.
     */
    public <T> T convert() throws NoConversionAvailableException, ConversionFailedException {

        Class<?> to = this.to;
        if (to == null) {
            to = Object.class;
        }

        ArooaConverter converter = session.getTools().getArooaConverter();

        return converter.convert(value, to);
    }

    public Class<?> getTo() {
        return to;
    }

    public void setTo(Class<?> to) {
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
