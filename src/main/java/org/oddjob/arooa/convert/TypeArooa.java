package org.oddjob.arooa.convert;

import java.lang.reflect.Type;

/**
 * Abstraction of a Java Type. This is to allow for Generic Conversion and various mechanisms of being
 * an {@link org.oddjob.arooa.ArooaValue}.
 *
 * @param <T>
 */
public interface TypeArooa<T> {

    Type getType();

    Class<T> getRawType();

    boolean isArooaValue();

    boolean isAssignableFrom(TypeArooa<?> other);

    static <T> TypeArooa<T> of(Class<T> type) {
        return LangTypeArooa.of(type);
    }

    static <T> TypeArooa<T> of(Type type) {
        return LangTypeArooa.of(type);
    }

    static <T> TypeArooa<T> ofArooaValue(Type type) {
        return LangTypeArooa.ofArooaValue(type);
    }

}
