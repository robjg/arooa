package org.oddjob.arooa.convert;

import org.oddjob.arooa.convert.doc.ItemAccessStrategy;
import org.oddjob.arooa.convert.doc.ValueFactoryItemAccess;
import org.oddjob.arooa.types.ValueFactory;

import java.lang.reflect.Method;

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

    public <T> ConversionStep<ValueFactory<?>, T> lastStep(
            final Class <? extends ValueFactory<?>> from,
            final Class<T> to,
            ConversionLookup conversions) {

        // Get the return type.
        Class returnType = toValueMethod(from).getReturnType();

        // Is there a conversion path from the type of the
        // factory to the required to type.
        final ConversionPath<Object, T> finalConversion =
                conversions.findConversion(returnType, to);

        if (finalConversion == null) {
            return null;
        }

        return new ConversionStep<>() {
            public Class<ValueFactory<?>> getFromClass() {
                return (Class) ValueFactory.class;
            }
            public Class<T> getToClass() {
                return to;
            }
            public T convert(ValueFactory<?> from, ArooaConverter converter)
                    throws ArooaConversionException {

                Object value = from.toValue();

                return finalConversion.convert(value, converter);
            }
        };
    }

    public static <F extends ValueFactory<?>> Method toValueMethod(Class<F> valueFactoryClass) {
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
