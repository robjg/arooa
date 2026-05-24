package org.oddjob.arooa.convert;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Our implementation of {@link ParameterizedType}
 *
 * @see TypeArooaUtils
 */
public class ParameterizedTypeArooa implements ParameterizedType {

    private final Class<?> rawType;

    private final Type[] actualTypeArguments;

    protected ParameterizedTypeArooa(Class<?> rawType,
                                  Type[] actualTypeArguments) {
        this.rawType = rawType;
        this.actualTypeArguments = actualTypeArguments;
    }

    public static ParameterizedType of(Class<?> rawType, Type... actualTypeArguments) {
        return new ParameterizedTypeArooa(Objects.requireNonNull(rawType),
                actualTypeArguments);
    }

    @NotNull
    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Class<?> getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(actualTypeArguments) ^
                Objects.hashCode(rawType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ParameterizedType that) {

            if (this == that)
                return true;

            if (that.getOwnerType() != null) {
                return false;
            }
            return rawType.equals(that.getRawType()) &&
                    Arrays.equals(actualTypeArguments, that.getActualTypeArguments());
        } else
            return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(rawType.getName());
        StringJoiner sj = new StringJoiner(", ", "<", ">");
        for(Type t: actualTypeArguments) {
            sj.add(t.getTypeName());
        }
        sb.append(sj);
        return sb.toString();
    }
}
