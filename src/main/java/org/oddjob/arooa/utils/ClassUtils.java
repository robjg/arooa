package org.oddjob.arooa.utils;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ClassResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Various utility methods relating to class.
 *
 * @author Rob Gordon.
 */
public class ClassUtils {
    private static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);

    /**
     * Primitive type to wrapper class type.
     */
    public static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap =
            Map.of(void.class, Void.class,
                    boolean.class, Boolean.class,
                    byte.class, Byte.class,
                    char.class, Character.class,
                    double.class, Double.class,
                    float.class, Float.class,
                    int.class, Integer.class,
                    long.class, Long.class,
                    short.class, Short.class);

    /**
     * Primitive type class names to types.
     */
    public static final Map<String, Class<?>> primitiveNameToTypeMap =
            primitiveTypeToWrapperMap.entrySet()
                    .stream()
                    .collect(Collectors.toMap(entry -> entry.getKey().getName(), Map.Entry::getKey));

    /**
     * Wrapper class type to primitive type.
     */
    private static final Map<Class<?>, Class<?>> wrapperToPrimitiveTypeMap =
            primitiveTypeToWrapperMap.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    /**
     * Provide the primitive class for the name. The name being int, short
     * etc.
     *
     * @param className The class name.
     * @return The primitive type. Null if no type exists.
     */
    public static Class<?> primitiveTypeForName(String className) {
        return primitiveNameToTypeMap.get(className);
    }

    /**
     * Provide the wrapper class for a primitive type.
     *
     * @param primitiveType
     * @return The wrapper class or null if the provided class is not
     * a primitive type.
     */
    public static Class<?> wrapperClassForPrimitive(Class<?> primitiveType) {
        return primitiveTypeToWrapperMap.get(primitiveType);
    }

    /**
     * Provide the primitive type for a wrapper class.
     *
     * @param wrapperType
     * @return The primitive type or null if the provided class is not
     * a wrapper class.
     */
    public static Class<?> primitiveTypeForWrapper(Class<?> wrapperType) {
        return wrapperToPrimitiveTypeMap.get(wrapperType);
    }

    /**
     * Same as {@link Class#forName} except that exception logs the class loader stack before
     * crashing.
     *
     * @param className The class name.
     * @param loader    The class loader.
     * @return The class if it exists;
     * @throws ClassNotFoundException
     */
    public static Class<?> classFor(String className, ClassLoader loader)
            throws ClassNotFoundException {
        if (className == null) {
            throw new NullPointerException("No class name.");
        }

        if (primitiveNameToTypeMap.containsKey(className)) {
            return primitiveNameToTypeMap.get(className);
        }

        try {
            return Class.forName(className, true, loader);
        } catch (Error | ClassNotFoundException e) {
            errorMessage(loader, e);
            throw e;
        }
    }

    /**
     * String array to class array.
     *
     * @param classNames The class names.
     * @param loader     The classloader to use.
     * @return Class array.
     * @throws ClassNotFoundException If a class isn't found.
     */
    public static Class<?>[] classesFor(String[] classNames, ClassLoader loader) throws ClassNotFoundException {

        Class<?>[] classes = new Class<?>[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            classes[i] = classFor(classNames[i], loader);
        }

        return classes;
    }


    /**
     * String array to class array with an {@link ClassResolver}.
     *
     * @param classNames The class names.
     * @param loader     The Class Resolver to use.
     * @return Class array.
     * @throws ClassNotFoundException If a class isn't found.
     */
    public static Class<?>[] classesFor(String[] classNames, ClassResolver loader) throws ClassNotFoundException {

        Class<?>[] classes = new Class<?>[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            String className = classNames[i];
            Class<?> cl = loader.findClass(className);
            if (cl == null) {
                throw new ClassNotFoundException(className);
            }
            classes[i] = cl;
        }

        return classes;
    }

    /**
     * Convenience method to convert and array of classes to an array of strings.
     *
     * @param classes The class array.
     * @return The strings.
     */
    public static String[] classesToStrings(Class<?>[] classes) {
        String[] strings = new String[classes.length];
        for (int i = 0; i < strings.length; ++i) {
            strings[i] = classes[i].getName();
        }
        return strings;
    }

    /**
     * Report an exception and print the class loader stack to the logger.
     *
     * @param classLoader The classLoader. May be null.
     * @param t           The exception. May not be null.
     */
    private static void errorMessage(ClassLoader classLoader, Throwable t) {
        logger.error("Exception [" + t.toString() + "] on it's way. " +
                (classLoader == null ? "The class loader is null, maybe that's why." :
                        "Here's the class loader stack:"));
        for (ClassLoader next = classLoader; next != null; next = next.getParent()) {
            logger.error("\t" + next);
        }
    }

    /**
     * Instantiates a Class but converts the exception if it fails.
     *
     * @param className
     * @param loader
     * @return
     * @throws ArooaException
     */
    public static Object instantiate(String className, ClassLoader loader)
            throws ArooaException {
        try {
            Class<?> cl = classFor(className, loader);
            return cl.getConstructor().newInstance();
        } catch (Exception e) {
            throw new ArooaException("Failed creating class [" + className + "]", e);
        }
    }

    /**
     * Cast an Object to the type including primitive types. The standard {@link Class#cast(Object)} method
     * won't cope with primitive type casting its wrapper. This simple little bodge gets round that.
     *
     * @param ignored The class which may be primitive. For type inference only.
     * @param object  The object wrapper.
     * @param <T>     The type.
     * @return An object cast to the correct type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Class<T> ignored, Object object) {

        return (T) object;
    }

    /**
     * Try and work out the simple name from anonymous classes and the like.
     *
     * @param cl
     *
     * @return The simple name, may be empty but not null.
     */
    public static String getSimpleName(Class<?> cl) {

        // returns an empty string if the class is anonymous
        String simpleName = cl.getSimpleName();
        if (simpleName.isEmpty()) {
            if (cl.getEnclosingClass() != null) {
                simpleName = cl.getEnclosingClass().getSimpleName();
            } else {
                // Can't work it out just give the full class.
                simpleName = cl.getName();
            }
        }
        return simpleName;
    }

    /**
     * Return the raw Type of Type which is either already a raw type or a Parameterised type such as
     * List&lt;String&gt; which would be List.
     *
     * @param type The type which may be generic or not.
     * @return The Raw type.
     * @throws IllegalArgumentException if the type is not a Class or Parameterised Type.
     */
    public static Class<?> rawType(Type type) throws IllegalArgumentException {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            throw new IllegalArgumentException("Can't work out raw type of [" + type + "]");
        }
    }

    /**
     * Get the contained type of a container class such as list that is the nth parameter of a method.
     * Probably contained or element type would be a better name for this method.
     *
     * @param method          The method.
     * @param parameterNumber The parameter to get the element type of.
     * @return The type that is the first generic parameter of the argument or null if it is raw.
     */
    public static Class<?> getComponentTypeOfParameter(Method method, int parameterNumber) {

        Type[] genericParameterTypes = method.getGenericParameterTypes();

        if (genericParameterTypes[parameterNumber] instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericParameterTypes[parameterNumber];

            Type[] typeArgs = parameterizedType.getActualTypeArguments();
            if (typeArgs.length == 0) {
                return null;
            }
            return rawType(typeArgs[0]);
        } else {
            return null;
        }
    }

    /**
     * Provide a dump of the class loader and its parents. Used for debug messages.
     *
     * @param classLoader The classloader. Maybe be null.
     *
     * @return Text of the stack containing newline characters.
     */
    public static String classLoaderStack(ClassLoader classLoader, String name) {

        StringBuilder builder = new StringBuilder(Objects.requireNonNullElse(name, "ClassLoader"));
        builder.append(" Stack:\n");
        if (classLoader == null) {
            builder.append(" null");
        } else {

            for (ClassLoader next = classLoader; next != null; next = next.getParent()) {
                builder.append("  ");
                builder.append(next);
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    /**
     * Provide a dump of the class loader of a class and its parents. Used for debug messages.
     *
     * @param ofClass The class. must not be null.
     *
     * @return Text of the stack containing newline characters.
     */
    public static String classLoaderStack(Class<?> ofClass) {

        return classLoaderStack(ofClass.getClassLoader(), ofClass.getName() + " ClassLoader");
    }

    /**
     * Provide a dump of the class loader of a class and the thread context loader and their parents.
     * Used for debug messages.
     *
     * @param ofClass The class. Must not be null.
     *
     * @return Text of the stack containing newline characters.
     */
    public static String classLoaderAndContextLoaderStack(Class<?> ofClass) {
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader = ofClass.getClassLoader();
        if (contextLoader == classLoader) {
            return classLoaderStack(classLoader, ofClass.getName() + " and Context ClassLoader");
        }
        return classLoaderStack(ofClass) +
                classLoaderStack(contextLoader, "ContextLoader");
    }
}
