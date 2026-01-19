package org.oddjob.arooa.convert.doc;

import org.oddjob.arooa.convert.ClassOrMethod;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.Joker;

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

    private final Map<ClassOrMethod, I> specified = new HashMap<>();

    private final Set<String> specifiedTypes = new HashSet<>();

    private final Map<FromTo, I> contents = new HashMap<>();

    private final ConversionItemProvider<I> factory;

    record FromTo(Type from, Type to) {}

    class Registration implements ConversionRegistry {

        @Override
        public <F, T> void register(Class<F> from, Class<T> to,
                                    Convertlet<F, T> convertlet) {

            I contents = factory.forConvertlet(from, to, convertlet);

            ClassOrMethod classOrMethod = ClassOrMethod.ofClass(convertlet.getClass());

            add(new FromTo(from, to), classOrMethod, contents);
        }

        @Override
        public <F> void registerJoker(Class<F> from, Joker<F> joker) {

            I contents = factory.forJoker(from, joker);

            ClassOrMethod classOrMethod = ClassOrMethod.ofClass(joker.getClass());

            add(new FromTo(from, null), classOrMethod, contents);
        }
    }

    void add(FromTo fromTo, ClassOrMethod classOrMethod, I content) {

        contents.put(fromTo, content);
        if (classOrMethod != null) {
            specified.put(classOrMethod, content);
            specifiedTypes.add(classOrMethod.getCanonicalClassName());
        }
    }

    public StandardItemAccess(ConversionItemProvider<I> factory) {
        this.factory = factory;
    }

    @Override
    public I getForType(String canonicalTypeName) {
        return specified.get(ClassOrMethod.ofCanonicalClassName(canonicalTypeName));
    }

    @Override
    public I getForMethod(String canonicalTypeName, String methodName) {
        return specified.get(ClassOrMethod.ofTypeAndMethodNames(canonicalTypeName, methodName));
    }

    @Override
    public boolean containsForType(String canonicalTypeName) {
        return specifiedTypes.contains(canonicalTypeName);
    }

    @Override
    public List<I> getAll() {
        return new ArrayList<>(contents.values());
    }
}
