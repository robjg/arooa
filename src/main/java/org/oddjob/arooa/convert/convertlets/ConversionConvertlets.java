package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionProviderFactory;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.utils.ClassUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Provide a conversion from a String which is a Classname of an {@link ConversionProvider}
 * to the provider. This is to support migration of the descriptor from class name conversions
 * to {@link ConversionProviderFactory}s.
 */
public class ConversionConvertlets implements ConversionProvider {

    @Override
    public void registerWith(ConversionRegistry registry) {
        registry.register(String.class, ConversionProviderFactory.class,
                from -> classLoader -> {
                    try {
                        return (ConversionProvider) ClassUtils.classFor(from, classLoader).getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException e) {
                        throw new IllegalArgumentException(e);
                    }
                });
    }
}
