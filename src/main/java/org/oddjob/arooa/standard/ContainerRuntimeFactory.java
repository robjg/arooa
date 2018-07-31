package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.PropertyIdentifier;

class ContainerRuntimeFactory {

	private final ElementAction<InstanceConfiguration> valueElementAction;
	
	private final ElementAction<InstanceConfiguration> componentElementAction;
	
	ContainerRuntimeFactory(
			ElementAction<InstanceConfiguration> valueElementAction,
			ElementAction<InstanceConfiguration> componentElementAction) {
		this.valueElementAction = valueElementAction;
		this.componentElementAction = componentElementAction;
	}
	
	class ActionFactory
	implements PropertyIdentifier.ElementActionFactory<InstanceConfiguration> {
		
		public ElementAction<InstanceConfiguration> createComponentElementAction() {

			return componentElementAction;		
		}
				
		public ElementAction<InstanceConfiguration> createValueElementAction() {

			return valueElementAction;
		}
	}
	
	/**
	 * 
	 * @author rob
	 *
	 */
	static class PropertyTypeSelector 
	implements PropertyIdentifier.PropertyTypeActions<ContainerRuntime, InstanceConfiguration> {
		
		
		public ContainerRuntime onIndexedElement(ArooaElement element, 
				ArooaContext context, ElementAction<InstanceConfiguration> action) 
		throws ArooaPropertyException {

			if (element.getAttributes().getAttributNames().length > 0) {
				throw new ArooaException(
						"Property name element [" + element.getTag() + 
						"] does not take attributes.");
			}
			
			return new IndexedPropertyRuntime(action, 
					new PropertyDefinitionImpl(element.getTag(), context), 
					context);
		}
		
		public ContainerRuntime onMappedElement(ArooaElement element, 
				ArooaContext context, ElementAction<InstanceConfiguration> action) 
		throws ArooaPropertyException {
			
			if (element.getAttributes().getAttributNames().length > 0) {
				throw new ArooaException(
						"Property name element [" + element.getTag() + 
						"] does not take attributes.");
			}
			
			return new MappedPropertyRuntime(action, 
					new PropertyDefinitionImpl(element.getTag(), context), 
					context);
			
		}
		
		public ContainerRuntime onVariantElement(ArooaElement element, ArooaContext context, 
				ElementAction<InstanceConfiguration> action) 
		throws ArooaPropertyException {

			if (element.getAttributes().getAttributNames().length > 0) {
				throw new ArooaException(
						"Property name element [" + element.getTag() + 
						"] does not take attributes.");
			}
			
			return new SimplePropertyRuntime(action, 
					new PropertyDefinitionImpl(element.getTag(), context), 
					context);
		}
	}
	
	private final PropertyIdentifier<ContainerRuntime, InstanceConfiguration> propertyIdentifier = 
		new PropertyIdentifier<ContainerRuntime, InstanceConfiguration>(
				new ActionFactory(),
				new PropertyTypeSelector());
	
	ContainerRuntime runtimeForProperty(
			ArooaElement element, ArooaContext parentContext) 
	throws ArooaConfigurationException {
				
		return propertyIdentifier.identifyPropertyFor(
				parentContext.getRuntime().getClassIdentifier(), 
						element, parentContext);
	}
}
