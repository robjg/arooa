package org.oddjob.arooa.convert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provide an {@link ConversionProvider} via reflection.
 */
public class ReflectionConversionProvider implements ConversionProviderFactory {

    private final Class<?> providerClass;

    private final Method method;

    public ReflectionConversionProvider(Class<?> providerClass, Method method) {
        this.providerClass = providerClass;
        this.method = method;
    }

    @Override
    public ConversionProvider createConversionProvider(ClassLoader classLoader) {
        return registry -> {
            Class<?> to = method.getReturnType();

            registerWithInferredTypes(registry, providerClass, to);
        };
    }

    <F, T> void registerWithInferredTypes(ConversionRegistry registry,
                                   Class<F> from, Class<T> to) {
        registry.register(from,
                to,
                fromObject -> {
                    try {
                        return to.cast(method.invoke(fromObject));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new ArooaConversionException(e);
                    }
                });
    }
}
