package org.oddjob.arooa.utils;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Helper to find Annotations.
 */
@FunctionalInterface
public interface AnnotationFinder {

    ArooaAnnotations findFor(Class<?> type);

    static AnnotationFinder forSession(ArooaSession session) {

        PropertyAccessor accessor =
                session.getTools().getPropertyAccessor();

        return type -> {

            ArooaBeanDescriptor beanDescriptor =
                    session.getArooaDescriptor().getBeanDescriptor(
                            new SimpleArooaClass(type),
                            accessor);

            return beanDescriptor.getAnnotations();
        };
    }

}
