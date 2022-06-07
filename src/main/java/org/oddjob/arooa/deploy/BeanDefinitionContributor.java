package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ConfiguredHow;

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

            Optional.ofNullable(propertyDefinition.getFlavour())
                    .ifPresent(flavour -> accumulator.setFlavour(propertyName, flavour));

            if (propertyDefinition.getAuto()) {
                accumulator.setAuto(propertyName);
            }
        }
    }
}
