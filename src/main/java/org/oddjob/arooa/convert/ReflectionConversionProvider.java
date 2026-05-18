package org.oddjob.arooa.convert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Provide an {@link ConversionProvider} via reflection.
 */
public class ReflectionConversionProvider implements ConversionProviderFactory {

    private final Class<?> providerClass;

    private final Method method;

    private final boolean arooaValue;

    public ReflectionConversionProvider(Class<?> providerClass,
                                        Method method,
                                        boolean arooaValue) {
        this.providerClass = providerClass;
        this.method = method;
        this.arooaValue = arooaValue;
    }

    @Override
    public ConversionProvider createConversionProvider(ClassLoader classLoader) {
        return registry -> {
            Type to = method.getGenericReturnType();

            registerWithInferredTypes(registry, providerClass, to);
        };
    }

    <F> void registerWithInferredTypes(ConversionRegistry registry,
                                   Class<F> from, Type to) {

        TypeArooa<F> fromType = this.arooaValue ?
                TypeArooas.ofArooaValue(from) : TypeArooas.of(from);

        registry.register(fromType,
                TypeArooa.of(to),
                fromObject -> {
                    try {
                        return method.invoke(fromObject);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new ArooaConversionException(e);
                    }
                });
    }
}
