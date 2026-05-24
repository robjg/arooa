package org.oddjob.arooa.convert;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.*;
import java.util.Arrays;

/**
 * Type Utilities for Arooa. These don't quite follow the rules of
 * Java but provide enough for reasonable type conversions.
 */
public class TypeArooaUtils {

    public static Type arooaEquivalent(Type type) {

        if (type instanceof Class<?> cl) {
            if (cl.isPrimitive()) {
                return ClassUtils.primitiveToWrapper(cl);
            } else {
                return cl;
            }
        }

        if (type instanceof ParameterizedType pt) {
            Type[] equivalentActual = Arrays.stream(pt.getActualTypeArguments())
                    .map(TypeArooaUtils::arooaEquivalent).toArray(Type[]::new);

            return ParameterizedTypeArooa.of((Class<?>) pt.getRawType(),
                    equivalentActual);
        }

        if (type instanceof GenericArrayType gat) {
            return Array.newInstance(rawType(gat.getGenericComponentType()), 0).getClass();
        }

        if (type instanceof WildcardType wt) {
            if (wt.getUpperBounds().length == 0) {
                throw new IllegalArgumentException("Wildcard type must have at least one upper bound: " + wt.getTypeName());
            }
            return arooaEquivalent(wt.getUpperBounds()[0]);
        }

        if (type instanceof TypeVariable<?> wt) {
            return Object.class;
        }

        return null;
    }


    public static Class<?> rawType(Type type) {

        if (type instanceof Class<?> cl) {
            return cl;
        }

        if (type instanceof ParameterizedType pt) {
            return (Class<?>) pt.getRawType();
        }

        if (type instanceof GenericArrayType gat) {
            return Array.newInstance(rawType(gat.getGenericComponentType()), 0).getClass();
        }

        if (type instanceof WildcardType wt) {
            if (wt.getUpperBounds().length == 0) {
                throw new IllegalArgumentException("Wildcard type must have at least one upper bound: " + wt.getTypeName());
            }
            return rawType(wt.getUpperBounds()[0]);
        }

        if (type instanceof TypeVariable<?> wt) {
            return Object.class;
        }

        return null;
    }

    public static boolean isAssignable(Type target, Type source) {

        if (target instanceof Class<?> targetCl) {
            Class<?> sourceCl = rawType(source);
            if (targetCl.isArray()) {
                if (sourceCl.isArray()) {
                    return targetCl.getComponentType().isAssignableFrom(sourceCl.getComponentType());
                }
                else {
                    return false;
                }
            }
            else {
                return targetCl.isAssignableFrom(sourceCl);
            }
        }

        if (target instanceof ParameterizedTypeArooa targetPt) {
            if (source instanceof Class<?> sourceCl) {
                return targetPt.getRawType().isAssignableFrom(sourceCl);
            }
            else if (source instanceof ParameterizedTypeArooa spt) {
                if (!targetPt.getRawType().isAssignableFrom(spt.getRawType())) {
                    return false;
                }

                if (targetPt.getActualTypeArguments().length != spt.getActualTypeArguments().length) {
                    return false;
                }

                for (int i = 0; i < targetPt.getActualTypeArguments().length; i++) {

                    if (!isAssignable(targetPt.getActualTypeArguments()[i], spt.getActualTypeArguments()[i])) {
                        return false;
                    }
                }

                return true;
            }
            else {
                return isAssignable(target, arooaEquivalent(source));
            }
        }

        return isAssignable(arooaEquivalent(target), source);
    }
}