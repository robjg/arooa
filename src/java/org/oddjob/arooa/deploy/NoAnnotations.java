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
}
