package org.oddjob.arooa.deploy;

import org.oddjob.arooa.reflect.ArooaClass;

/**
 * Provides a way to accumulate how properties are configured from various sources.
 *
 * @see BeanDescriptorBuilder
 * @see DefaultBeanDescriptorProvider
 */
public interface ConfiguredHowAccumulator {

    ArooaClass getClassIdentifier();

    void addElementProperty(String propertyName);

    void addAttributeProperty(String propertyName);

}
