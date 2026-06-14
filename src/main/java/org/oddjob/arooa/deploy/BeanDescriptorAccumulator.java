package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ParsingInterceptor;

import java.lang.annotation.Annotation;

/**
 * Provides a way to accumulate contributions to an {@link org.oddjob.arooa.ArooaBeanDescriptor} from
 * various sources.
 *
 * @see BeanDescriptorAccumulator
 * @see BeanDescriptorBuilder
 *
 * @author Rob
 */
public interface BeanDescriptorAccumulator extends ConfiguredHowAccumulator {

    void setComponentProperty(String propertyName);

    void setTextProperty(String propertyName);

    void addHiddenProperty(String propertyName);

    void setQualifier(String propertyName, Annotation qualifier);

    void setAuto(String propertyName);

    void setParsingInterceptor(ParsingInterceptor parsingInterceptor);

}
