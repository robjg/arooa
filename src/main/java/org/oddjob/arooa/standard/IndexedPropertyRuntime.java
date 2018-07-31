package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.reflect.ArooaPropertyException;

class IndexedPropertyRuntime extends ContainerRuntime {

	private final ElementAction<InstanceConfiguration> childCreator;

	/**
	 * 
	 * @param childCreator
	 * @param runtimeClass
	 * @param property
	 */
	public IndexedPropertyRuntime(
			ElementAction<InstanceConfiguration> childCreator, 
			PropertyDefinition propertyDefinition,
			ArooaContext parentContext) {
		super(propertyDefinition, parentContext);
		this.childCreator = childCreator;
	}

	@Override
	ArooaHandler getHandler() {
		return new ArooaHandler() {
			public ArooaContext onStartElement(ArooaElement element, 
					ArooaContext parentContext) 
			throws ArooaConfigurationException {
	
				final InstanceConfiguration child = childCreator.onElement(
						element,
						parentContext);
	
				
				InstanceRuntime runtime = new IndexItemRuntime(
						child, parentContext);
				
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
	public void setIndexedProperty(String name, int index, Object value) 
	throws ArooaPropertyException {
		getParentContext().getRuntime().setIndexedProperty(
				getPropertyDefinition().getPropertyName(), 
				index, 
				convert(value));
	}
}
