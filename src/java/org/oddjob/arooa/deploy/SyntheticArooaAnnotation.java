package org.oddjob.arooa.deploy;

import java.lang.annotation.Annotation;

/**
 * An {@link ArooaAnnotation} created just from a String from a 
 * descriptor.
 * 
 * @author rob
 *
 */
public class SyntheticArooaAnnotation implements ArooaAnnotation {
	
	private final String annotationName;
	
	/**
	 * Constructor.
	 * 
	 * @param annotationName The name of the annotation.
	 */
	public SyntheticArooaAnnotation(String annotationName) {
		this.annotationName = annotationName;
	}
	
	@Override
	public String getName() {
		return annotationName;
	}
	
	@Override
	public <T extends Annotation> T realAnnotation(Class<T> annotationType) {
		return null;
	}

}
