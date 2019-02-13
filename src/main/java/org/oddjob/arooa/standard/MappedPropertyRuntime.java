package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaConstants;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.reflect.ArooaPropertyException;

/**
 * A {@link org.oddjob.arooa.runtime.RuntimeConfiguration} for an
 * indexed property.
 *
 * @author Rob
 */
class MappedPropertyRuntime extends ContainerRuntime {

	/** Creates the configuration for the instance that is the value of this
	 * property. */
	private final ElementAction<InstanceConfiguration> nestedAction;

	/**
	 * Constructor.
	 *
	 * @param nestedAction The child Instance Configuration creator.
	 * @param propertyDefinition The definition of the property this is for.
	 * @param parentContext The parent context.
	 */
	public MappedPropertyRuntime(
			ElementAction<InstanceConfiguration> nestedAction,
			PropertyDefinition propertyDefinition,
			ArooaContext parentContext) {
		super(propertyDefinition, parentContext);
		
		this.nestedAction = nestedAction;
	}

	@Override
	ArooaHandler getHandler() {
		return new ArooaHandler() {
		
			public ArooaContext onStartElement(ArooaElement element, 
					ArooaContext parentContext) 
			throws ArooaConfigurationException {

				String key = element.getAttributes().get(ArooaConstants.KEY_PROPERTY);
	
				final InstanceConfiguration child = nestedAction.onElement(
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
