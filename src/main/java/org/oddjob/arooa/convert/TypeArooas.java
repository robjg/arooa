package org.oddjob.arooa.convert;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.oddjob.arooa.ArooaValue;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public class TypeArooas {

    @SuppressWarnings("unchecked")
    public static <T> TypeArooa<T> of(Type type) {
        Class<T> rawType;
        if (type instanceof Class<?> cl) {
            if (cl.isPrimitive()) {
                Class<?> wrapper = ClassUtils.primitiveToWrapper(cl);
                return (TypeArooa<T>) new RawTypeArooa<>(wrapper);
            } else {
                rawType = (Class<T>) cl;
            }
        } else {
            rawType = rawType(type);
        }

        if (ArooaValue.class.isAssignableFrom(rawType)) {
            return new ArooaValueType<>(rawType);
        }

        if (type == rawType) {
            return new RawTypeArooa<>(rawType);
        }

        if (type instanceof ParameterizedType pt) {
            return new ParameterizedTypeArooa<>(pt, rawType);
        }

        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    public static <T> TypeArooa<T> ofArooaValue(Type type) {
        if (type instanceof Class<?> cl && cl.isPrimitive()) {
            throw new IllegalArgumentException("Primitive type " + cl.getName() + " can not be an ArooaValue.");
        }

        return new ArooaValueType<>(rawType(type));
    }

    @SuppressWarnings("unchecked")
    static <T> Class<T> rawType(Type type) {
        return Objects.requireNonNull((Class<T>)
                        TypeUtils.getRawType(Objects.requireNonNull(type), null),
                "Can't extract raw type from " + type.getTypeName());
    }

    abstract static class AbstractTypeArooa<T> implements TypeArooa<T> {

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TypeArooa<?> that)) return false;
            return Objects.equals(getType(), that.getType());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getType());
        }

        @Override
        public String toString() {
            return "TypeArooa{" +
                    "type=" + getType().getTypeName() +
                    '}';
        }

    }

    static class RawTypeArooa<T> extends AbstractTypeArooa<T> {

        private final Class<T> rawType;

        RawTypeArooa(Class<T> rawType) {
            this.rawType = rawType;
        }

        @Override
        public Type getType() {
            return rawType;
        }

        @Override
        public Class<T> getRawType() {
            return rawType;
        }

        @Override
        public boolean isArooaValue() {
            return false;
        }

        @Override
        public boolean isAssignableFrom(TypeArooa<?> other) {
            return getRawType().isAssignableFrom(other.getRawType()) &&
                    isArooaValue() == other.isArooaValue();
        }
    }

    static class ArooaValueType<T> extends RawTypeArooa<T> {

        ArooaValueType(Class<T> rawType) {
            super(rawType);
        }

        @Override
        public boolean isArooaValue() {
            return true;
        }
    }

    static class ParameterizedTypeArooa<T> extends AbstractTypeArooa<T> {

        private final Type type;

        private final Class<T> rawType;

        public ParameterizedTypeArooa(ParameterizedType type, Class<T> rawType) {
            this.type = type;
            this.rawType = rawType;
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
            return false;
        }

        @Override
        public boolean isAssignableFrom(TypeArooa<?> other) {

            return TypeUtils.isAssignable(other.getType(), this.type);
        }

    }
}
