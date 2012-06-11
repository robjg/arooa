package org.oddjob.arooa.deploy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate that a property is configured by XML text. 
 * 
 * @see ArooaBeanDescriptor#getTextProperty();
 * 
 * @author rob
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface ArooaText {
	
}
