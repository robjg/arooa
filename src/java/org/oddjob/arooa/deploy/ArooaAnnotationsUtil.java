package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaAnnotations;

/**
 * Utility class for accessing {@link ArooaAnnotations}
 * 
 * @author rob
 *
 */
public class ArooaAnnotationsUtil {

	private final ArooaAnnotations annotations;
	
	public ArooaAnnotationsUtil(ArooaAnnotations arooaAnnotations) {
		this.annotations = arooaAnnotations;
	}
	
	public String findSingleAnnotatedProperty(
			String annotationName) {
		
		String[] properties = annotations.annotatedProperties();
		
		String result = null;
		
		for (String property : properties) {
			
			ArooaAnnotation arooaAnnotation = 
					annotations.annotationForProperty(
							property, annotationName);
			
			if (arooaAnnotation != null) {
				if (result == null) {
					result = property;
				}
				else {
					throw new IllegalStateException(
							"More than one property annotated with " + 
							annotationName);
				}
			}
		}
		
		return result;
	}
}
