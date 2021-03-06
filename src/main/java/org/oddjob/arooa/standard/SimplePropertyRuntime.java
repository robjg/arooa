package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.runtime.ConfigurationNodeEvent;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;
import org.oddjob.arooa.runtime.ModificationRefusedException;

/**
 * A {@link org.oddjob.arooa.runtime.RuntimeConfiguration} for an
 * simple property.
 *
 * @author rob
 *
 */
class SimplePropertyRuntime extends ContainerRuntime {


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
	public SimplePropertyRuntime(
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
				final InstanceConfiguration instance = nestedAction.onElement(element, parentContext);

				InstanceRuntime runtime = new SimpleInstanceRuntime(instance, parentContext);
				
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
	public void setProperty(String name, Object value) 
	throws ArooaPropertyException {
		getParentContext().getRuntime().setProperty(
				getPropertyDefinition().getPropertyName(), 
				convert(value));
	}
	
	@Override
	void setContext(ArooaContext context) 
	throws ArooaConfigurationException {
		super.setContext(context);
		context.getConfigurationNode().addNodeListener(
				new ConfigurationNodeListener() {
					int count;
					
					public void insertRequest(
							ConfigurationNodeEvent nodeEvent)
							throws ModificationRefusedException {
						if (count > 0) {
							throw new ModificationRefusedException(
									"A simple property can only have one value.",
									nodeEvent);
						}
					}
					
					public void removalRequest(ConfigurationNodeEvent nodeEvent)
							throws ModificationRefusedException {
					}
					
					public void childInserted(ConfigurationNodeEvent nodeEvent) {
						++count;
					}
					public void childRemoved(ConfigurationNodeEvent nodeEvent) {
						--count;
					}
				});
	}
	
}
