package org.oddjob.arooa.convert.doc;

import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.Joker;
import org.oddjob.arooa.convert.ValueFactoryConversion;
import org.oddjob.arooa.types.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provide an index of Conversion Docs for {@link ValueFactory}
 * conversions.
 *
 * @param <I> The type of item indexed. Typically {@link org.oddjob.arooa.beandocs.ConversionDoc}.
 */
public class ValueFactoryItemAccess<I> implements ConversionItemAccess<I> {

    private static final Logger logger = LoggerFactory.getLogger(ValueFactoryItemAccess.class);

    public static final ItemAccessStrategy strategy =

            new ItemAccessStrategy() {

                @Override
                public <X> ConversionRegistry processIn(StrategyContext<X> context,
                                                        ConversionItemProvider<X> factory) {

                    context.supplyIfAbsent(ValueFactoryItemAccess.class,
                            () -> new ValueFactoryItemAccess<>(factory));

                    return new Registration();
                }
            };

    private final Map<Class<? extends ValueFactory<?>>, I> contents = new HashMap<>();

    private final ConversionItemProvider<I> factory;

    static class Registration implements ConversionRegistry {

        @Override
        public <F, T> void register(Class<F> from, Class<T> to,
                                    Convertlet<F, T> convertlet) {
        }

        @Override
        public <F> void registerJoker(Class<F> from, Joker<F> joker) {
        }
    }

    public ValueFactoryItemAccess(ConversionItemProvider<I> factory) {
        this.factory = factory;
    }

    @Override
    public I getForType(TypeIdentifier typeIdentifier) {
        return maybe(typeIdentifier);
    }

    I maybe(TypeIdentifier typeIdentifier) {

        Class<? extends ValueFactory<?>> valueFactoryClass = valueFactoryClassFor(typeIdentifier);
        if (valueFactoryClass == null || Modifier.isAbstract(valueFactoryClass.getModifiers())) {
            return null;
        }

        return contents.computeIfAbsent(valueFactoryClass, ignored ->
                factory.create(valueFactoryClass,
                        ValueFactoryConversion.toValueMethod(valueFactoryClass).getReturnType(),
                        typeIdentifier));
    }

    @Override
    public I getForMethod(MethodIdentifier methodIdentifier) {
        return null;
    }

    @Override
    public boolean containsForType(TypeIdentifier typeIdentifier) {

        return maybe(typeIdentifier) != null;
    }

    @Override
    public List<I> getAll() {
        return new ArrayList<>(contents.values());
    }

    Class<? extends ValueFactory<?>> valueFactoryClassFor(TypeIdentifier typeIdentifier) {

        try {
            Class<?> cl = Class.forName(typeIdentifier.getClassName());

            if (ValueFactory.class.isAssignableFrom(cl)) {
                //noinspection unchecked
                return (Class<? extends ValueFactory<?>>) cl;
            }
            else {
                return null;
            }
        } catch (Throwable e) {
            logger.warn("Failed to load class for: {}", typeIdentifier, e);
            return null;
        }
    }

}
