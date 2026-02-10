package org.oddjob.arooa.convert.doc;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * An Identifier for a method for indexing conversion docs.
 */
public abstract class MethodIdentifier implements ElementIdentifier {

    public static MethodIdentifier ofMethod(Method method) {
        return new AsMethod(method);
    }

    public abstract String getMethodName();

    @Override
    public String getName() {
        return getTypeIdentifier().getClassName() + "#" + getMethodName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTypeIdentifier(), getMethodName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof MethodIdentifier other) {
            return other.getTypeIdentifier().equals(getTypeIdentifier())
                    && other.getMethodName().equals(getMethodName());
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + getName() + '\'' +
                '}';
    }

    static class AsMethod extends MethodIdentifier {

        private final Method method;

        AsMethod(Method method) {
            this.method = method;
        }

        @Override
        public String getMethodName() {
            return method.getName();
        }

        @Override
        public TypeIdentifier getTypeIdentifier() {
            return TypeIdentifier.ofClass(method.getDeclaringClass());
        }
    }

}
