package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ParsingInterceptor;

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

    void setFlavour(String propertyName, String flavour);

    void setAuto(String propertyName);

    void setParsingInterceptor(ParsingInterceptor parsingInterceptor);

}
