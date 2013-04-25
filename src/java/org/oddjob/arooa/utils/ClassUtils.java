package org.oddjob.arooa.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaException;

/**
 * Various utility methods relating to class.
 * 
 * @author Rob Gordon.
 */
public class ClassUtils {
	private static Logger logger = Logger.getLogger(ClassUtils.class);
	
	/**
	 * Primitive type class names to types.
	 */
	private static final Map<String, Class<?>> primitiveNameToTypeMap = 
			new HashMap<String, Class<?>>(8);
	
	/**
	 * Primitive type to wrapper class type.
	 */
	private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = 
			new HashMap<Class<?>, Class<?>>(8);

	/**
	 * Wrapper class type to primitive type.
	 */
	private static final Map<Class<?>, Class<?>> wrapperToPrimitiveTypeMap = 
			new HashMap<Class<?>, Class<?>>(8);
	
	static {
		Class<?>[] primatives = {
				boolean.class, byte.class, char.class, double.class,
				float.class, int.class, long.class, short.class };

		Class<?>[] wrappers = {
				Boolean.class, Byte.class, Character.class, Double.class,
				Float.class, Integer.class, Long.class, Short.class };
		
		for (int i = 0; i < 8; ++i) {
			primitiveNameToTypeMap.put(primatives[i].getName(), 
					primatives[i]);
		}		
		
		for (int i = 0; i < 8; ++i) {
			primitiveTypeToWrapperMap.put(primatives[i], wrappers[i]);
		}		
		
		for (int i = 0; i < 8; ++i) {
			wrapperToPrimitiveTypeMap.put(wrappers[i], primatives[i]);
		}		
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
	 * Provide the primiative type for a wrapper class.
	 * 
	 * @param wrapperType
	 * 
	 * @return The primitive type or null if the provided class is not 
	 * a wrapper class.
	 */
	public static Class<?> primiativeTypeForWrapper(Class<?> wrapperType) {
		return wrapperToPrimitiveTypeMap.get(wrapperType);
	}
	
	/**
	 * Same as Class.forName exception logs the class loader stack before
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
	
	private static void errorMessage(ClassLoader classLoader, Throwable t) {
		logger.error(t.toString() + " on it's way. Here's the ClassLoader stack:");
    	for (ClassLoader next = classLoader; next != null; next = next.getParent()) {
    		logger.error("  " + next);
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
	
}
