package org.oddjob.arooa.deploy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.oddjob.arooa.ArooaBeanDescriptor;

/**
 * Annotate that a property is configured as an XML element.
 * 
 * @see ArooaBeanDescriptor#getConfiguredHow(String)
 * 
 * @author rob
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface ArooaElement {
	
}
