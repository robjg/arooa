package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.*;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaLifeAware;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.parsing.ArooaElement;

/**
 * @author rob
 * @oddjob.description Register a value with an id.
 * <p>
 * Unlike components, values can't have an id. This type allows
 * values to be registered so they can
 * be referenced via the given id elsewhere in the configuration.
 * <p>
 * Components are registered when the configuration is parsed
 * but the given value will only be registered during the configuration
 * phase, such as when a job runs in Oddjob.
 * <p>
 * @oddjob.example Register a value.
 * <p>
 * {@oddjob.xml.resource org/oddjob/arooa/types/IdentifiableValueTypeExample.xml}
 *
 */
public class IdentifiableValueType
        implements ArooaValue, ArooaSessionAware, ArooaLifeAware {

    public static final ArooaElement ELEMENT = new ArooaElement("identify");

    static class IdentifiableValueTypeJoke implements Joker<IdentifiableValueType> {

        @Override
        public <T> ConversionStep<IdentifiableValueType, T>
        lastStep(ConversionPath<?, IdentifiableValueType> pathBefore,
                 TypeArooa<IdentifiableValueType> from,
                 TypeArooa<T> to,
                 ConversionLookup conversions) {

            return new ConversionStep<>() {

                @Override
                public Class<IdentifiableValueType> getFromClass() {
                    return IdentifiableValueType.class;
                }

                @Override
                public TypeArooa<IdentifiableValueType> getFromType() {
                    return from;
                }

                @Override
                public Class<T> getToClass() {
                    return to.getRawType();
                }

                @Override
                public TypeArooa<T> getToType() {
                    return to;
                }

                public T convert(IdentifiableValueType from, ArooaConverter converter)
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
            registry.registerJoker(IdentifiableValueType.class,
                    new IdentifiableValueTypeJoke());
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
            } else {
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

    @Override
    public String toString() {
        return "IdentifiableValueType{" +
                "id='" + id + '\'' +
                '}';
    }
}
