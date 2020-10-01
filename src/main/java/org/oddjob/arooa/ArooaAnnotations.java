package org.oddjob.arooa;

import org.oddjob.arooa.deploy.ArooaAnnotation;

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
	Method methodFor(String annotationName);

	/**
	 * Provide all properties that are annotated.
	 * 
	 * @return Array of properties or an empty array. Never null.
	 */
	String[] annotatedProperties();
	
	/**
	 * Get the Annotation for a property by annotation name.
	 * 
	 * @param propertyName The name of the property.
	 * @param annotationName The name of the annotation.
	 * 
	 * @return The annotation, or null if one doesn't exist.
	 */
	ArooaAnnotation annotationForProperty(String propertyName,
			String annotationName);
	
	/**
	 * Get all the annotations for a property.
	 * 
	 * @param propertyName The property name.
	 * 
	 * @return An array of annotations, may be empty, never null.
	 */
	ArooaAnnotation[] annotationsForProperty(String propertyName);

}
