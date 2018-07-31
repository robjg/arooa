package org.oddjob.arooa.deploy;

import java.lang.annotation.Annotation;

/**
 * An {@link ArooaAnnotation} created from a real annotation.
 * 
 * @author rob
 *
 */
public class AnnotationArooaAnnotation implements ArooaAnnotation {

	private final Annotation annotation;
	
	/**
	 * Constructor.
	 * 
	 * @param annotation The underlying annotation.
	 */
	public AnnotationArooaAnnotation(Annotation annotation) {
		if (annotation == null) {
			throw new NullPointerException("No Annotation");
		}
		this.annotation = annotation;
	}
	
	@Override
	public String getName() {
		return annotation.annotationType().getName();
	}
	
	@Override
	public <T extends Annotation> T realAnnotation(Class<T> annotationType) {
		return annotationType.cast(annotation);
	}
}
