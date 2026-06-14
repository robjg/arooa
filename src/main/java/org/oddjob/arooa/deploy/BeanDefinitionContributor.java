package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ConfiguredHow;

import javax.inject.Named;
import javax.inject.Qualifier;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Contributes to a {@link BeanDescriptorAccumulator} from a {@link BeanDefinition}.
 */
public class BeanDefinitionContributor implements BeanDescriptorContributor {

    public void makeContribution(BeanDefinition beanDefinition,
                                 BeanDescriptorAccumulator accumulator) {

        Optional.ofNullable(beanDefinition.getInterceptor())
                        .ifPresent(accumulator::setParsingInterceptor);

        Collection<PropertyDefinitionBean> propertyDefinitionBeans
                = beanDefinition.toPropertyDefinitions();

        for (PropertyDefinitionBean propertyDefinition : propertyDefinitionBeans) {

            String propertyName = Objects.requireNonNull(
                    propertyDefinition.getName(), "No name for a property definition.");

            ConfiguredHow configuredHow = propertyDefinition.getConfiguredHow();
            if (configuredHow == null) {
                // Must be an attribute definition.
                continue;
            }

            if (propertyDefinition.isComponentProperty()) {
                accumulator.setComponentProperty(propertyName);
            }
            else {
                switch (configuredHow) {
                    case ATTRIBUTE:
                        accumulator.addAttributeProperty(propertyName);
                        break;
                    case ELEMENT:
                        accumulator.addElementProperty(propertyName);
                        break;
                    case TEXT:
                        accumulator.setTextProperty(propertyName);
                        break;
                    case HIDDEN:
                        accumulator.addHiddenProperty(propertyName);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown type " + configuredHow);
                }
            }

            String flavour = propertyDefinition.getFlavour();
            if (flavour != null) {
                try {
                    Named annotation = (Named) SyntheticAnnotation.with()
                            .value(flavour)
                            .named(Named.class.getName());
                    accumulator.setQualifier(propertyName, (Qualifier) annotation);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            if (propertyDefinition.getAuto()) {
                accumulator.setAuto(propertyName);
            }
        }
    }
}
