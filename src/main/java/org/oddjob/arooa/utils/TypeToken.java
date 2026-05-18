package org.oddjob.arooa.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Poor man's equivalent of others elsewhere. Trying to reduce dependencies.
 */
abstract public class TypeToken<T> {

    private final Type type;

    protected TypeToken() {
        this.type = getTypeArgument();
    }

    public Type getType() {
        return type;
    }

    protected Type getTypeArgument() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        else {
            throw new IllegalArgumentException("Unexpected Generic Superclass " + type.getTypeName());
        }
    }
}
