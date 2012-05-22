package org.oddjob.arooa;

import java.lang.reflect.Method;

/**
 * Capture annotation information about a bean.
 * 
 * @author rob
 *
 */
public interface ArooaAnnotations {

	/**
	 * Get the method for an annotation name.
	 * 
	 * @param annotationName The annotation name.
	 * 
	 * @return the method for the annotation name, or null if there
	 * isn't one.
	 */
	public Method methodFor(String annotationName);

}
