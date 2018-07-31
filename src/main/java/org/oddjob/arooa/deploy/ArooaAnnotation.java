package org.oddjob.arooa.deploy;

import java.lang.annotation.Annotation;

/**
 * Wrapper for an annotation. Allows a pretend annotation to be
 * declared in an Arooa Descriptor file.
 * 
 * @author rob
 *
 */
public interface ArooaAnnotation {

	/**
	 * Get the name.
	 * 
	 * @return The name of annotation.
	 */
	String getName();
		
	/**
	 * Provide the real annotation if this is a wrapper for a real 
	 * annotation. Null otherwise.
	 *  
	 * @param annotationType The annotation this might be a wrapper for.
	 * 
	 * @return The annotation or null.
	 */
	<T extends Annotation> T realAnnotation(Class<T> annotationType);

}
