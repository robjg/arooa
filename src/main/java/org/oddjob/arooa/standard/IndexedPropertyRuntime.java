package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.reflect.ArooaPropertyException;

import java.util.Objects;

/**
 * A {@link org.oddjob.arooa.runtime.RuntimeConfiguration} for an
 * indexed property.
 *
 * @author Rob
 */
class IndexedPropertyRuntime extends ContainerRuntime {

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
	public IndexedPropertyRuntime(
			ElementAction<InstanceConfiguration> nestedAction,
			PropertyDefinition propertyDefinition,
			ArooaContext parentContext) {

		super(propertyDefinition, parentContext);

        Objects.requireNonNull(nestedAction);
		this.nestedAction = nestedAction;
	}

	@Override
	ArooaHandler getHandler() {
		return new ArooaHandler() {
			public ArooaContext onStartElement(ArooaElement element, 
					ArooaContext parentContext) 
			throws ArooaConfigurationException {
	
				final InstanceConfiguration child = nestedAction.onElement(
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
