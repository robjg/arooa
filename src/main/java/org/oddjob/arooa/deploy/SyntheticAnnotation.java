package org.oddjob.arooa.deploy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public class SyntheticAnnotation {

    static class IH implements InvocationHandler {

        private final Class<? extends Annotation> type;

        private final Object value;

        private IH(Class<? extends Annotation> type, Object value) {
            this.type = Objects.requireNonNull(type);
            this.value = value;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

            String name = method.getName();
            int parameterCount = method.getParameterCount();

            if (parameterCount == 1 && "equals".equals(name)
                    && method.getParameterTypes()[0].equals(Object.class)) {

                return equalsImpl(proxy, args[0]);
            }

            if (parameterCount != 0) {
                throw new AssertionError("Too many parameters for an annotation method");
            }

            return switch (name) {
                case "hashCode" -> hashCodeImpl();
                case "toString" -> toStringImpl();
                case "annotationType" -> type;
                case "value" -> value;
                default -> throw new NoSuchMethodException("unknown method: " + name);
            };
        }

        boolean equalsImpl(Object proxy, Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

            if (proxy == obj) {
                return true;
            }

            if (!type.isInstance(obj)) {
                return false;
            }

            if (value == null) {
                return true;
            }

            return value.equals(type.getMethod("value").invoke(obj));
        }

        int hashCodeImpl() {
            // matching Real Annotation hash code.
            return value == null ? 0 : (127 * "value".hashCode()) ^
                    value.hashCode();
        }

        String toStringImpl() {
            return '@' +
                    // Guard against null canonical name; shouldn't happen
                    Objects.toString(type.getCanonicalName(),
                            "<no canonical name>") +
                    '(' +
                    (value == null ? "" : ("" + '"' + value + '"'))
                    + ')';
        }
    }

    public static class Builder {

        private Object value;

        private ClassLoader classLoader;

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        public Builder classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Annotation named(String annotationName)
                throws ClassNotFoundException {

            ClassLoader cl = Objects.requireNonNullElseGet(this.classLoader,
                    () -> getClass().getClassLoader());

            @SuppressWarnings("unchecked")
            Class<? extends Annotation> type = (Class<? extends Annotation>) Class.forName(annotationName, true, cl);

            return of(type);
        }

        public Annotation of(Class<? extends Annotation> type) {

            ClassLoader cl = Objects.requireNonNullElseGet(this.classLoader,
                    () -> getClass().getClassLoader());

            return (Annotation) Proxy.newProxyInstance(cl, new Class[]{Annotation.class, type},
                    new IH(type, value));
        }
    }

    public static Builder with() {
        return new Builder();
    }

    public static <T extends Annotation> T named(String annotationName) throws ClassNotFoundException {
        //noinspection unchecked
        return (T) with().named(annotationName);
    }

 }
