package org.oddjob.arooa.utils;

import org.oddjob.arooa.ArooaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Various utility methods relating to class.
 * 
 * @author Rob Gordon.
 */
public class ClassUtils {
	private static Logger logger = LoggerFactory.getLogger(ClassUtils.class);
	
	/**
	 * Primitive type class names to types.
	 */
	private static final Map<String, Class<?>> primitiveNameToTypeMap =
			new HashMap<>(9);
	
	/**
	 * Primitive type to wrapper class type.
	 */
	private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap =
			new HashMap<>(9);

	/**
	 * Wrapper class type to primitive type.
	 */
	private static final Map<Class<?>, Class<?>> wrapperToPrimitiveTypeMap =
			new HashMap<>(9);
	
	static {
		Class<?>[] primitives = {
				void.class,
				boolean.class, byte.class, char.class, double.class,
				float.class, int.class, long.class, short.class };

		Class<?>[] wrappers = {
				Void.class,
				Boolean.class, Byte.class, Character.class, Double.class,
				Float.class, Integer.class, Long.class, Short.class };
		
		for (int i = 0; i < primitives.length; ++i) {
			primitiveNameToTypeMap.put(primitives[i].getName(),
					primitives[i]);
		}		
		
		for (int i = 0; i < primitives.length; ++i) {
			primitiveTypeToWrapperMap.put(primitives[i], wrappers[i]);
		}		
		
		for (int i = 0; i < primitives.length; ++i) {
			wrapperToPrimitiveTypeMap.put(wrappers[i], primitives[i]);
		}		
	}

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
	 * 
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
	 * 
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
	 * @param className
	 * @param loader
	 * @return
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
		}
		
		catch (Error e) {
			errorMessage(loader, e);
			throw e;
		}
		catch (ClassNotFoundException e) {
			errorMessage(loader, e);
			throw e;
		}
	}

	/**
	 * String array to class array.
	 *
	 * @param classNames The class names.
	 * @param loader The classloader to use.
	 *
	 * @return Class array.
	 *
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
	 * @param t The exception. May not be null.
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
	 * 
	 * @throws ArooaException
	 */
	public static Object instantiate(String className, ClassLoader loader) 
	throws ArooaException {
		try {
			return classFor(className, loader).newInstance();
		} catch (Exception e) {
			throw new ArooaException("Failed creating class [" + className +"]", e);
		}
	}

	/**
	 * Cast an Object to the type including primitive types. The standard {@link Class#cast(Object)} method
	 * won't cope with primitive type casting it's wrapper. This simple little bodge gets round that.
	 *
	 * @param ignored The class which may be primitive. For type inference only.
	 * @param object The object wrapper.
	 *
	 * @param <T> The type.
	 *
	 * @return An object cast to the correct type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Class<T> ignored, Object object) {

		return (T) object;
	}
	
}
