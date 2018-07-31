package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.BeanDescriptorHelper;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * Handles the {@link ArooaElement} for a property.
 * 
 * @author rob
 *
 */
public class PropertyHandler implements ArooaHandler {
	
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
	
		BeanDescriptorHelper propertyHelper = new BeanDescriptorHelper(
				beanDescriptor);
		
		if (!propertyHelper.isElement(propertyName)) {
			throw new ArooaException("Property " + propertyName +
					" is not configured as an element.");
		}

		ArooaType type = propertyHelper.getArooaType(propertyName);
		
		final ContainerRuntime propertyRuntime = containerRuntimeFactory
				.runtimeForProperty(
						element, parentContext);

		StandardConfigurationNode node = new StandardConfigurationNode(
				element) {
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
			public ConfigurationHandle parse(
					ArooaContext parentContext)
			throws ArooaParseException {
				if (children().length == 0) {
					return null;
				}
				else {
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
