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
public class ClassesUtils {
	private static Logger logger = Logger.getLogger(ClassesUtils.class);
	
	/**
	 * Map with primitive type name as key and corresponding primitive
	 * type as value, for example: "int" -> "int.class".
	 */
	private static final Map<String, Class<?>> primitiveTypeNameMap = 
			new HashMap<String, Class<?>>(8);
	
	static {
		Class<?>[] primatives = {
				boolean.class, byte.class, char.class, double.class,
				float.class, int.class, long.class, short.class };

		for (Class<?> primative : primatives) {
			primitiveTypeNameMap.put(primative.getName(), primative);
		}		
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
		
		if (primitiveTypeNameMap.containsKey(className)) {
			return primitiveTypeNameMap.get(className);
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
