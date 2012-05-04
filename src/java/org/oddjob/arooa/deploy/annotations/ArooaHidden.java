package org.oddjob.arooa.deploy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate that a property should be hidden from the configuration. This
 * annotation must go on the property setter.
 * 
 * @see ArooaBeanDescriptor#getConfiguredHow(String)
 * 
 * @author rob
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ArooaHidden {
	
}
