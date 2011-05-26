package org.oddjob.arooa.utils;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaException;

/**
 * Various utility methods relating to class.
 * 
 * @author Rob Gordon.
 */
public class ClassesUtils {
	private static Logger logger = Logger.getLogger(ClassesUtils.class);
	
	public static Class<?> classFor(String className, ClassLoader loader) 
	throws ClassNotFoundException {
		if (className == null) {
			throw new NullPointerException("No class name.");
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
	
	public static Object instantiate(String className, ClassLoader loader) 
	throws ArooaException {
		try {
			return classFor(className, loader).newInstance();
		} catch (Exception e) {
			throw new ArooaException("Failed creating class [" + className +"]", e);
		}
	}
	
}
