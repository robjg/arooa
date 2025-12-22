package org.oddjob.arooa.convert;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.oddjob.arooa.ArooaValue;

import java.lang.reflect.Type;
import java.util.Objects;

public class LangTypeArooa<T> implements TypeArooa<T> {

    private final Type type;

    private final Class<T> rawType;

    private final boolean arooaValue;

    public LangTypeArooa(Type type, Class<T> rawType, boolean arooaValue) {
        this.type = type;
        this.rawType = rawType;
        this.arooaValue = arooaValue;
    }


    @SuppressWarnings("unchecked")
    public static <T> TypeArooa<T> of(Type type) {
        Class<T> rawType;
        if (type instanceof Class<?> cl) {
            if (cl.isPrimitive()) {
                Class<?> wrapper = ClassUtils.primitiveToWrapper(cl);
                return (TypeArooa<T>) new LangTypeArooa<>(wrapper, wrapper, false);
            } else {
                rawType = (Class<T>) cl;
            }
        } else {
            rawType = rawType(type);
        }

        if (ArooaValue.class.isAssignableFrom(rawType)) {
            return new LangTypeArooa<>(type, rawType, true);
        } else {
            return new LangTypeArooa<>(type, rawType, false);
        }
    }

    public static <T> TypeArooa<T> ofArooaValue(Type type) {
        if (type instanceof Class<?> cl && cl.isPrimitive()) {
            throw new IllegalArgumentException("Primitive type " + cl.getName() + " can not be an ArooaValue.");
        }
        return new LangTypeArooa<>(type, rawType(type), true);
    }

    @SuppressWarnings("unchecked")
    static <T> Class<T> rawType(Type type) {
        return Objects.requireNonNull((Class<T>)
                        TypeUtils.getRawType(Objects.requireNonNull(type), null),
                "Can't extract raw type from " + type.getTypeName());
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Class<T> getRawType() {
        return rawType;
    }

    @Override
    public boolean isArooaValue() {
        return arooaValue;
    }

    @Override
    public boolean isAssignableFrom(TypeArooa<?> other) {

        return TypeUtils.isAssignable(other.getType(), this.type);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TypeArooa<?> that)) return false;
        return Objects.equals(type, that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return "LangTypeArooa{" +
                "type=" + type +
                '}';
    }
}
