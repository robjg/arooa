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
	
	private final Annotation annotation;
	
	/**
	 * Constructor.
	 * 
	 * @param annotation The name of the annotation.
	 */
	private SyntheticArooaAnnotation(Annotation annotation) {
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

	public static SyntheticArooaAnnotation named(String name) throws ClassNotFoundException {
		return new SyntheticArooaAnnotation(SyntheticAnnotation.named(name));
	}

	public static SyntheticArooaAnnotation named(String name, ClassLoader classLoader) throws ClassNotFoundException {
		return new SyntheticArooaAnnotation(SyntheticAnnotation.with()
				.classLoader(classLoader).named(name));
	}
}
