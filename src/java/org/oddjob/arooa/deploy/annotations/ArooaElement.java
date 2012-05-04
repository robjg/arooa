package org.oddjob.arooa.deploy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.oddjob.arooa.ArooaBeanDescriptor;

/**
 * Annotate that a property is configured as an XML element. This 
 * annotation must go on the property setter.
 * 
 * @see ArooaBeanDescriptor#getConfiguredHow(String)
 * 
 * @author rob
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ArooaElement {
	
}
