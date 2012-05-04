package org.oddjob.arooa.deploy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate that a property is a Component property. This must go
 * on the setter method.
 * 
 * @see ArooaBeanDescriptor#getComponentProperty();
 * 
 * @author rob
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ArooaComponent {
}
