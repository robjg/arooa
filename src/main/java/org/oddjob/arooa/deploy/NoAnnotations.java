package org.oddjob.arooa.deploy;

import java.lang.reflect.Method;

import org.oddjob.arooa.ArooaAnnotations;


/**
 * A no-op implementation of {@link ArooaAnnotations}
 *  
 * @author rob
 *
 */
public class NoAnnotations implements ArooaAnnotations {

	@Override
	public Method methodFor(String annotationName) {
		return null;
	}
	
	@Override
	public String[] annotatedProperties() {
		return new String[0];
	}
	
	@Override
	public ArooaAnnotation[] annotationsForProperty(String propertyName) {
		return new ArooaAnnotation[0];
	}
	
	@Override
	public ArooaAnnotation annotationForProperty(String propertyName,
			String annotationName) {
		return null;
	}
	
	
	
}
