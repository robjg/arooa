package org.oddjob.arooa.deploy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.oddjob.arooa.ParsingInterceptor;

/**
 * An annotation that is the class of an {@link ParsingInterceptor} for
 * the annotated component. 
 * 
 * @author rob
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ArooaInterceptor {
	
	String value() default "";
}
