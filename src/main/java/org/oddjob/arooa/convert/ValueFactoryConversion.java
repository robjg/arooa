package org.oddjob.arooa.convert;

import org.oddjob.arooa.convert.doc.ItemAccessStrategy;
import org.oddjob.arooa.convert.doc.ValueFactoryItemAccess;
import org.oddjob.arooa.types.ValueFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Provide the conversion for an {@link ValueFactory}.
 */
public class ValueFactoryConversion implements Joker<ValueFactory<?>> {

    public static class Conversions implements ConversionProvider {

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public void registerWith(ConversionRegistry registry) {
            registry.registerJoker(ValueFactory.class,
                    (Joker) new ValueFactoryConversion());
        }
    }

    @Override
    public <T> ConversionStep<ValueFactory<?>, T> lastStep(ConversionPath<?, ValueFactory<?>> pathBefore,
                                                           TypeArooa<ValueFactory<?>> from,
                                                           TypeArooa<T> to,
                                                           ConversionLookup conversions) {

        ConversionStep<?, ?> stepBefore = pathBefore.getStep(pathBefore.length() - 1);

        // Get the return type.
        Type returnType = toValueMethod(stepBefore.getFromType().getRawType()).getGenericReturnType();

        // Is there a conversion path from the type of the
        // factory to the required to type.
        final ConversionPath<Object, T> finalConversion =
                conversions.findConversion(returnType, to.getType());

        if (finalConversion == null) {
            return null;
        }

        return new ConversionStep<>() {

            @Override
            public TypeArooa<ValueFactory<?>> getFromType() {
                return from;
            }

            @Override
            public TypeArooa<T> getToType() {
                return to;
            }

            @Override
            public T convert(ValueFactory<?> from, ArooaConverter converter)
                    throws ArooaConversionException {

                Object value = from.toValue();

                return finalConversion.convert(value, converter);
            }
        };
    }

    public static Method toValueMethod(Class<?> valueFactoryClass) {
        try {
            return valueFactoryClass.getMethod("toValue");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ItemAccessStrategy documentedHow() {
        return ValueFactoryItemAccess.strategy;
    }
}
