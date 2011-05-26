package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaConstants;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.reflect.ArooaPropertyException;

class MappedPropertyRuntime extends ContainerRuntime {

	private final ElementAction<InstanceConfiguration> childCreator;

	/**
	 * 
	 * @param childCreator
	 * @param runtimeClass
	 * @param property
	 */
	public MappedPropertyRuntime(
			ElementAction<InstanceConfiguration> childCreator, 
			PropertyDefinition propertyDefinition,
			ArooaContext context) {
		super(propertyDefinition, context);
		
		this.childCreator = childCreator;
	}

	@Override
	ArooaHandler getHandler() {
		return new ArooaHandler() {
		
			public ArooaContext onStartElement(ArooaElement element, 
					ArooaContext parentContext) 
			throws ArooaConfigurationException {

				String key = element.getAttributes().get(ArooaConstants.KEY_PROPERTY);
	
				final InstanceConfiguration child = childCreator.onElement(
						element, parentContext);

				child.getAttributeSetter().addOptionalAttribute(ArooaConstants.KEY_PROPERTY);
					
				InstanceRuntime runtime = new MapItemRuntime(key, child, parentContext);
				
	    		InstanceConfigurationNode node = new InstanceConfigurationNode(
	    				element, runtime);
	    		
				ArooaContext ourContext = new StandardArooaContext(
						parentContext.getArooaType(), runtime, node, parentContext);				
				
				runtime.setContext(ourContext);
				return runtime.getContext();
			}
		};
	}
	
	@Override
	public void setMappedProperty(String name, String key, Object value) 
	throws ArooaPropertyException {
		getParentContext().getRuntime().setMappedProperty(
				getPropertyDefinition().getPropertyName(), 
				key, 
				convert(value));
	}
	    
}
