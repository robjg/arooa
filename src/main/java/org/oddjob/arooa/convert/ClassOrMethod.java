package org.oddjob.arooa.convert;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Wraps a Class or Method as something that provides documentation for a conversion.
 *
 * @see org.oddjob.arooa.convert.doc.StandardItemAccess
 */
abstract public class ClassOrMethod {

    protected final String canonicalClassName;

    private ClassOrMethod(String canonicalClassName) {
        this.canonicalClassName = canonicalClassName;
    }

    public static ClassOrMethod ofClass(Class<?> type) {
        return new AsClass(type.getCanonicalName());
    }

    public static ClassOrMethod ofCanonicalClassName(String typeName) {
        return new AsClass(Objects.requireNonNull(typeName));
    }

    public static ClassOrMethod ofMethod(Method method) {
        return new ClassOrMethod.AsMethod(method.getDeclaringClass().getCanonicalName(),
                method.getName());
    }

    public static ClassOrMethod ofTypeAndMethodNames(String canonicalClassName, String methodName) {
        return new ClassOrMethod.AsMethod(Objects.requireNonNull(canonicalClassName),
                Objects.requireNonNull(methodName));
    }

    public String getCanonicalClassName() {
        return canonicalClassName;
    }

    abstract public String getName();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + getName() + '\'' +
                '}';
    }

    static private class AsClass extends ClassOrMethod {

        AsClass(String typeName) {
            super(typeName);
        }

        @Override
        public String getName() {
            return canonicalClassName;
        }

        @Override
        public int hashCode() {
            return Objects.hash(canonicalClassName);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof AsClass other) {
                return Objects.equals(canonicalClassName, other.canonicalClassName);
            }
            return false;
        }
    }

    static private class AsMethod extends ClassOrMethod {

        private final String methodName;

        AsMethod(String typeName, String methodName) {
            super(Objects.requireNonNull(typeName));
            this.methodName = Objects.requireNonNull(methodName);
        }

        @Override
        public String getName() {
            return canonicalClassName + "#" + methodName;
        }

        @Override
        public int hashCode() {
            return Objects.hash(canonicalClassName, methodName);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof AsMethod other) {
                return other.canonicalClassName.equals(canonicalClassName)
                        && other.methodName.equals(methodName);
            }
            return false;
        }
    }
}
