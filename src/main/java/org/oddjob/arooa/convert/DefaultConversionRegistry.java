/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;

import org.oddjob.arooa.ArooaValue;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Implementation of a ConvertletRegistry.
 *
 * @author rob
 *
 */
public class DefaultConversionRegistry implements ConversionRegistry, ConversionLookup {

    /**
     * Map of from class to possible Map of to class to Convertlet.
     * This is linked to preserve registration order during searches.
     */
    private final Map<TypeArooa<?>, Map<TypeArooa<?>, Convertlet<?, ?>>> fromMap =
            new LinkedHashMap<>();

    private final Map<Type, TypeArooa<?>> typeArooaMap = new HashMap<>();

    private final JokerMap jokers =
            new JokerMap();

    @Override
    public <F, T> void register(TypeArooa<F> from, TypeArooa<?> to,
                                Convertlet<F, T> convertlet) {
        fromMap.computeIfAbsent(from, k -> new LinkedHashMap<>())
                .put(to, convertlet);
        typeArooaMap.put(from.getType(), from);
    }

    @Override
    public <F> void registerJoker(Class<F> from, Joker<F> joker) {
        jokers.register(from, joker);
    }


    @Override
    public <F, T> ConversionPath<F, T> findConversion(Type from, Type to) {

        TypeArooa<F> fromType = typeArooaOf(from);
        return best(fromType, fromType, to,
                DefaultConversionPath.instance(fromType), 0);
    }

    <X> TypeArooa<X> typeArooaOf(Type type) {

        @SuppressWarnings("unchecked")
        TypeArooa<X> to = (TypeArooa<X>) typeArooaMap.get(type);
        return Objects.requireNonNullElseGet(to, () -> TypeArooa.of(type));
    }

    /**
     * Recursive function to find the best conversion path.
     *
     */
    @SuppressWarnings("unchecked")
    <F, X, Y, T> ConversionPath<F, T> best(final TypeArooa<X> start,
                                           final TypeArooa<X> from,
                                           final Type toType,
                                           ConversionPath<F, X> stepsSoFar,
                                           int maxLevels) {

        TypeArooa<T> to = typeArooaOf(toType);

        // have we reached the end of the conversion?
        // Only true for ArooaValues if the required is an ArooaValue
        if (to.isAssignableFrom(from)) {
            return (ConversionPath<F, T>) stepsSoFar;
        }

        Iterable<Joker<X>> jokersMatching = jokers.getMatching(from);
        for (Joker<X> joker : jokersMatching) {
            ConversionStep<X, T> step = joker.lastStep(start, to, this);

            if (step != null) {

                // joker trumps all.
                return stepsSoFar.append(step);
            }
        }

        // keep track of best levels to save us going down unnecessary paths.
        int bestLevels = maxLevels;
        ConversionPath<F, T> bestResult = null;


        // get the converters for the next step.
        Map<TypeArooa<?>, Convertlet<?, ?>> toConverters = fromMap.get(from);

        if (toConverters != null) {
            // iterate through all the possible conversions to.
            for (Map.Entry<TypeArooa<?>, Convertlet<?, ?>> entry : toConverters.entrySet()) {

                // the one to try the next
                final TypeArooa<Y> maybeTo = (TypeArooa<Y>) entry.getKey();

                final Convertlet<X, Y> convertlet = (Convertlet<X, Y>) entry.getValue();

                if (convertlet instanceof FinalConvertlet
                        && !to.equals(maybeTo)) {
                    // only use a final convertlet if it converts to
                    // the required class.
                    continue;
                }

                // work out what the next conversion steps would be
                ConversionStep<X, Y> nextStep = new ConversionStep<>() {
                    public Y convert(X from, ArooaConverter converter)
                            throws ArooaConversionException {
                        return convertlet.convert(from);
                    }

                    @Override
                    public Class<X> getFromClass() {
                        return from.getRawType();
                    }

                    @Override
                    public Class<Y> getToClass() {
                        return maybeTo.getRawType();
                    }

                    @Override
                    public TypeArooa<X> getFromType() {
                        return from;
                    }

                    @Override
                    public TypeArooa<Y> getToType() {
                        return maybeTo;
                    }
                };

                // recursively call. A non null result means we found a match.
                ConversionPath<F, T> result = nextBest(maybeTo,
                        stepsSoFar, nextStep, toType, bestLevels, true);

                if (result != null) {
                    // because of the check for best levels this result must
                    // now be the best. So remember it.
                    bestResult = result;
                    bestLevels = result.length() - stepsSoFar.length();
                }
            }
        }

        // Try a superclass conversion.
        Class<? super X>[] supers = extendsAndImplements(from);
        for (Class<? super X> aSuper : supers) {
            final TypeArooa<Y> superClass = (TypeArooa<Y>) TypeArooa.of(aSuper);

            // next conversion steps would be with super class
            ConversionStep<X, Y> nextStep = new ConversionStep<>() {
                @Override
                public Y convert(X from, ArooaConverter converter) {
                    return (Y) from;
                }

                @Override
                public Class<X> getFromClass() {
                    return from.getRawType();
                }

                @Override
                public Class<Y> getToClass() {
                    return superClass.getRawType();
                }

                @Override
                public TypeArooa<X> getFromType() {
                    return from;
                }

                @Override
                public TypeArooa<Y> getToType() {
                    return superClass;
                }
            };

            // recursively call. A non null result means we found a match.
            ConversionPath<F, T> result = nextBest((TypeArooa<Y>) from,
                    stepsSoFar, nextStep, toType, bestLevels, false);

            if (result != null) {
                // if the superclass gave us a result it must be a shorter path
                // so use it instead.
                bestResult = result;
            }
        }

        return bestResult;
    }

    /**
     * Utility function to calculate what a class extends and
     * implements.
     *
     * @param fromType The from type.
     * @return The things it extends and implements.
     */
    @SuppressWarnings("unchecked")
    static <S> Class<? super S>[] extendsAndImplements(TypeArooa<S> fromType) {
        Class<S> fromClass = fromType.getRawType();
        List<Class<?>> results = new ArrayList<>();
        Class<?> superClass = fromClass.getSuperclass();

        // Stop paths from an ArooaValue to a non ArooaValue object.
        if (superClass != null &&
                (ArooaValue.class.isAssignableFrom(fromClass) == ArooaValue.class.isAssignableFrom(superClass))) {
            results.add(superClass);
        }

        // An also stop ArooaValue to none ArooaValue interfaces (mainly for Serializable which would then
        // provide a path to Object
        Class<?>[] interfaces = fromClass.getInterfaces();
        for (Class<?> iface : interfaces) {
            if ((ArooaValue.class.isAssignableFrom(fromClass) == ArooaValue.class.isAssignableFrom(iface))) {
                results.add(iface);
            }
        }

        return results.toArray(new Class[0]);
    }

    /**
     * Utility method to create the new ConversionPath.
     *
     * @param stepsSoFar Steps now.
     * @param nextStep   Next Step to try.
     * @param to         The class to convert to.
     * @param maxLevels  The maximum number of levels to try.
     * @return The result.
     */
    <F, X, Y, T> ConversionPath<F, T> nextBest(TypeArooa<Y> start,
                                               ConversionPath<F, X> stepsSoFar,
                                               ConversionStep<X, Y> nextStep,
                                               Type to,
                                               int maxLevels,
                                               boolean allowJokers) {

        // maxLevels of one means our previous recursive call
        // actually found a perfect match.
        if (maxLevels == 1) {
            return null;
        }

        // check we're not going back on ourselves
        if (stepsSoFar.contains(nextStep.getToType())) {
            return null;
        }

        ConversionPath<F, Y> next = stepsSoFar.append(nextStep);

        return best(start, nextStep.getToType(), to, next, maxLevels - 1);
    }

    private static class JokerMap {

        private final Map<TypeArooa<?>, List<Joker<?>>> map =
                new LinkedHashMap<>();

        <From> void register(Class<From> from, Joker<From> joker) {
            register(TypeArooa.of(from), joker);
        }

        <From> void register(TypeArooa<From> from, Joker<From> joker) {
            map.computeIfAbsent(from, k -> new ArrayList<>())
                    .add(joker);
        }

        @SuppressWarnings("unchecked")
        <From> Iterable<Joker<From>> getMatching(TypeArooa<From> cl) {
            List<Joker<From>> results = new ArrayList<>();
            List<Joker<?>> jokersAny = map.get(cl);
            if (jokersAny != null) {
                // Why can't I do results.addAll((List<Joker<From>>)jokersAny)????
                for (Joker<?> jokerAny : jokersAny) {
                    Joker<From> joker = (Joker<From>) jokerAny;
                    results.add(joker);
                }
            }
            return results;
        }
    }
}
