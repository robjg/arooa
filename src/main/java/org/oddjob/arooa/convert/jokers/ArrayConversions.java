package org.oddjob.arooa.convert.jokers;

import org.oddjob.arooa.convert.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class ArrayConversions implements ConversionProvider {

    static class ArrayJoker implements Joker<Object> {

        @Override
        public <T> ConversionStep<Object, T> lastStep(ConversionPath<?, Object> pathBefore,
                                                      TypeArooa<Object> fromType,
                                                      TypeArooa<T> toType,
                                                      ConversionLookup conversions) {

            // Array conversions first go to object because there is no way to register a
            // conversion for all array types.
            TypeArooa<Object> originalFrom;
            if (pathBefore.length() == 0) {
                originalFrom = fromType;
            } else {
                originalFrom = pathBefore.getStep(pathBefore.length() - 1).getFromType();
            }
            Class<?> from = originalFrom.getRawType();

            Class<T> to = toType.getRawType();

            if (from.isArray() && to.isAssignableFrom(List.class)) {

                return new ConversionStep<>() {

                    @Override
                    public TypeArooa<Object> getFromType() {
                        return fromType;
                    }

                    @Override
                    public TypeArooa<T> getToType() {
                        return toType;
                    }

                    public T convert(Object from, ArooaConverter converter) {
                        final Object[] array = (Object[]) from;
                        //noinspection unchecked
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

                    @Override
                    public TypeArooa<Object> getFromType() {
                        return fromType;
                    }

                    @Override
                    public TypeArooa<T> getToType() {
                        return toType;
                    }

                    @Override
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

                //noinspection unchecked
                return (ConversionStep<Object, T>) toStringConversion(
                        fromType, from, conversions);
            }

            if (to.isArray()) {

                Class<?> toComponent = to.getComponentType();

                @SuppressWarnings("rawtypes") final ConversionPath componentPath =
                        conversions.findConversion(from, toComponent);

                if (componentPath == null) {
                    return null;
                }

                return new ConversionStep<>() {

                    @Override
                    public TypeArooa<Object> getFromType() {
                        return fromType;
                    }

                    @Override
                    public TypeArooa<T> getToType() {
                        return toType;
                    }

                    @Override
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

                        //noinspection unchecked
                        return (T) newArray;
                    }
                };
            }
            return null;
        }
    }

    public void registerWith(ConversionRegistry registry) {

        registry.registerJoker(Object.class, new ArrayJoker());
    }

    static <T> ConversionStep<?, String> toStringConversion(final TypeArooa<Object> fromType,
                                                            final Class<?> from,
                                                            ConversionLookup conversions) {

        TypeArooa<String> toType = TypeArooa.of(String.class);

        @SuppressWarnings("unchecked")
        Class<T> fromComponent = (Class<T>) from.getComponentType();

        final ConversionPath<T, String> componentConversion =
                conversions.findConversion(fromComponent, String.class);

        return new ConversionStep<>() {

            @Override
            public TypeArooa<Object> getFromType() {
                return fromType;
            }

            @Override
            public TypeArooa<String> getToType() {
                return toType;
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
