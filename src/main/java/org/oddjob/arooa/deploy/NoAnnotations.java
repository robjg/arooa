package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaAnnotations;

import java.lang.reflect.Method;


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
	public String propertyFor(String annotationName) {
		return null;
	}

	@Override
	public ArooaAnnotation annotationForProperty(String propertyName, String annotationName) {
		return null;
	}
}
