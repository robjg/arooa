package org.oddjob.arooa.standard;

import org.oddjob.arooa.*;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.ParseContext;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * Handles the {@link ArooaElement} for an instance.
 *
 * @see InstanceRuntime
 *
 * @author rob
 */
class PropertyOfInstanceHandler implements ArooaHandler {

    private final ContainerRuntimeFactory containerRuntimeFactory =
            new ContainerRuntimeFactory(
                    new ValueConfigurationCreator(),
                    new ComponentConfigurationCreator());

    public ArooaContext onStartElement(final ArooaElement element,
                                       ArooaContext parentContext) throws ArooaConfigurationException {

        String propertyName = element.getTag();

        ArooaSession session = parentContext.getSession();

        ArooaClass runtimeClass = parentContext.getRuntime(
        ).getClassIdentifier();

        ArooaBeanDescriptor beanDescriptor =
                session.getArooaDescriptor().getBeanDescriptor(
                        runtimeClass, session.getTools().getPropertyAccessor());

        if (beanDescriptor.getConfiguredHow(propertyName) != ConfiguredHow.ELEMENT) {
            throw new ArooaException("Property " + propertyName +
                                             " is not configured as an element.");
        }

        ArooaType type = beanDescriptor.getArooaType(propertyName);

        final ContainerRuntime propertyRuntime = containerRuntimeFactory
                .runtimeForProperty(
                        element, parentContext);

        StandardConfigurationNode node = new StandardConfigurationNode(
                ()-> element) {
            public void addText(String text) {
                if (text.trim().length() > 0) {
                    throw new ArooaException(
                            "Property element " + element + " does not support text: " +
                                    text);
                }
            }

            @Override
            public String getText() {
                return null;
            }

            public ArooaContext getContext() {
                return propertyRuntime.getContext();
            }

            @Override
            public <P extends ParseContext<P>> ConfigurationHandle<P> parse(
                    P parentContext)
                    throws ArooaParseException {
                if (children().length == 0) {
                    return null;
                } else {
                    return super.parse(parentContext);
                }
            }
        };

        ArooaContext propertyContext = new StandardArooaContext(
                type, propertyRuntime, node, parentContext);

        propertyRuntime.setContext(propertyContext);

        return propertyRuntime.getContext();
    }

}
