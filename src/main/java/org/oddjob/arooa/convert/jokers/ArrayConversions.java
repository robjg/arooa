package org.oddjob.arooa.convert.jokers;

import org.oddjob.arooa.convert.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class ArrayConversions implements ConversionProvider {

    public void registerWith(ConversionRegistry registry) {

        registry.registerJoker(Object.class, new Joker<>() {

            @SuppressWarnings("unchecked")
            public <T> ConversionStep<Object, T> lastStep(
                    Class<?> from,
                    final Class<T> to,
                    ConversionLookup conversions) {

                if (from.isArray() && to.isAssignableFrom(List.class)) {

                    return new ConversionStep<>() {
                        public Class<Object> getFromClass() {
                            return Object.class;
                        }

                        public Class<T> getToClass() {
                            return to;
                        }

                        public T convert(Object from, ArooaConverter converter) {
                            final Object[] array = (Object[]) from;
                            return (T) Arrays.asList(array);
                        }
                    };
                }
                if (from.isArray() && to.isArray()) {

                    Class<?> fromComponent = from.getComponentType();
                    Class<?> toComponent = to.getComponentType();

                    @SuppressWarnings("rawtypes") final ConversionPath componentPath =
                            conversions.findConversion(fromComponent, toComponent);

                    if (componentPath == null) {
                        return null;
                    }

                    return new ConversionStep<>() {
                        public Class<Object> getFromClass() {
                            return Object.class;
                        }

                        public Class<T> getToClass() {
                            return to;
                        }

                        public T convert(Object from, ArooaConverter converter)
                                throws ArooaConversionException {
                            int arrayLength = Array.getLength(from);
                            Object newArray = Array.newInstance(
                                    to.getComponentType(), arrayLength);
                            for (int i = 0; i < arrayLength; ++i) {
                                Object element = Array.get(from, i);
                                Object convertedElement;
                                try {
                                    convertedElement = componentPath.convert(element, converter);
                                } catch (ArooaConversionException e) {
                                    throw new ConvertletException(e);
                                }
                                if (convertedElement != null) {
                                    Array.set(newArray, i,
                                            convertedElement);
                                }
                            }

                            return (T) newArray;
                        }
                    };
                }
                if (from.isArray() && to.isAssignableFrom(String.class)) {

                    return (ConversionStep<Object, T>) toStringConversion(from, conversions);
                }
                if (to.isArray()) {

                    Class<?> toComponent = to.getComponentType();

                    @SuppressWarnings("rawtypes") final ConversionPath componentPath =
                            conversions.findConversion(from, toComponent);

                    if (componentPath == null) {
                        return null;
                    }

                    return new ConversionStep<>() {
                        public Class<Object> getFromClass() {
                            return Object.class;
                        }

                        public Class<T> getToClass() {
                            return to;
                        }

                        public T convert(Object from, ArooaConverter converter)
                                throws ArooaConversionException {
                            Object newArray = Array.newInstance(
                                    to.getComponentType(), 1);

                            Object convertedElement;
                            try {
                                convertedElement = componentPath.convert(from, converter);
                            } catch (ArooaConversionException e) {
                                throw new ConvertletException(e);
                            }
                            if (convertedElement != null) {
                                Array.set(newArray, 0,
                                        convertedElement);
                            }

                            return (T) newArray;
                        }
                    };
                }
                return null;
            }
        });
    }

    static <T> ConversionStep<?, String> toStringConversion(
            final Class<?> fromType, ConversionLookup conversions) {

        @SuppressWarnings("unchecked")
        Class<T> fromComponent = (Class<T>) fromType.getComponentType();

        final ConversionPath<T, String> componentConversion =
                conversions.findConversion(fromComponent, String.class);

        return new ConversionStep<>() {

            @Override
            public Class<Object> getFromClass() {
                return Object.class;
            }

            @Override
            public Class<String> getToClass() {
                return String.class;
            }

            @Override
            public String convert(Object from, ArooaConverter converter) throws ArooaConversionException {

                Object[] fromArray = (Object[]) from;

                StringBuilder builder = new StringBuilder();
                for (Object element : fromArray) {
                    if (!builder.isEmpty()) {
                        builder.append(", ");
                    }

                    @SuppressWarnings("unchecked")
                    String elementStr = componentConversion.convert((T) element, converter);
                    builder.append(elementStr);
                }
                return builder.toString();
            }
        };
    }

}
