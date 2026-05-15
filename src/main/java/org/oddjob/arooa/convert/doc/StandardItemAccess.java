package org.oddjob.arooa.convert.doc;

import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.Joker;
import org.oddjob.arooa.convert.TypeArooa;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Access for a typical Conversion.
 *
 * @param <I> The type of item being accessed.
 */
public class StandardItemAccess<I> implements ConversionItemAccess<I> {

    public static final ItemAccessStrategy strategy =

            new ItemAccessStrategy() {

                @Override
                public <X> ConversionRegistry processIn(StrategyContext<X> context,
                                                        ConversionItemProvider<X> factory) {

                    StandardItemAccess<X> container = context.supplyIfAbsent(StandardItemAccess.class,
                            () -> new StandardItemAccess<>(factory));

                    return container.new Registration();

                }
            };

    private final Map<ElementIdentifier, I> specified = new HashMap<>();

    private final Set<TypeIdentifier> specifiedTypes = new HashSet<>();

    private final Map<FromTo, I> contents = new HashMap<>();

    private final ConversionItemProvider<I> factory;

    record FromTo(Type from, Type to) {}

    class Registration implements ConversionRegistry {

        @Override
        public <F, T> void register(TypeArooa<F> from, TypeArooa<?> to,
                                    Convertlet<F, T> convertlet) {

            Type fromType = from.getType();
            Type toType = to.getType();

            TypeIdentifier typeIdentifier = TypeIdentifier.ofClass(convertlet.getClass());

            I contents = factory.create(fromType, toType, typeIdentifier);

            add(new FromTo(fromType, toType), typeIdentifier, contents);
        }

        @Override
        public <F> void registerJoker(Class<F> from, Joker<F> joker) {

            TypeIdentifier typeIdentifier = TypeIdentifier.ofClass(joker.getClass());

            I contents = factory.create(from, null, typeIdentifier);

            add(new FromTo(from, null), typeIdentifier, contents);
        }
    }

    void add(FromTo fromTo, ElementIdentifier elementIdentifier, I content) {

        contents.put(fromTo, content);
        if (elementIdentifier != null) {
            specified.put(elementIdentifier, content);
            specifiedTypes.add(elementIdentifier.getTypeIdentifier());
        }
    }

    public StandardItemAccess(ConversionItemProvider<I> factory) {
        this.factory = factory;
    }

    @Override
    public I getForType(TypeIdentifier typeIdentifier) {
        return specified.get(typeIdentifier);
    }

    @Override
    public I getForMethod(MethodIdentifier methodIdentifier) {
        return specified.get(methodIdentifier);
    }

    @Override
    public boolean containsForType(TypeIdentifier typeIdentifier) {
        return specifiedTypes.contains(typeIdentifier);
    }

    @Override
    public List<I> getAll() {
        return new ArrayList<>(contents.values());
    }
}
