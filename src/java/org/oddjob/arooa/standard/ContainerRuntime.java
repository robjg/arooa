package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListener;

/**
 * Base class for runtimes that contain properties.
 * 
 * @author rob
 *
 */
abstract class ContainerRuntime extends StandardRuntime {	
	
	private final PropertyDefinition propertyDefinition;
	
	private final RuntimeListener runtimeListener = 
		new RuntimeListener() {
		public void beforeInit(RuntimeEvent event) 
		throws ArooaConfigurationException {
			fireBeforeInit();				
		}
		public void afterInit(RuntimeEvent event)
		throws ArooaConfigurationException {
			fireAfterInit();
		}
		
		public void beforeConfigure(RuntimeEvent event) 
		throws ArooaConfigurationException {
			fireBeforeConfigure();
		}
		public void afterConfigure(RuntimeEvent event)
		throws ArooaConfigurationException {
			fireAfterConfigure();
		}
		public void beforeDestroy(RuntimeEvent event) 
		throws ArooaConfigurationException {
			fireBeforeDestroy();
		}
		public void afterDestroy(RuntimeEvent event)
		throws ArooaConfigurationException {
			fireAfterDestroy();
		}
	};

	
	public ContainerRuntime(
			PropertyDefinition propertyDefinition,
			ArooaContext parentContext) {
		super(parentContext);
		this.propertyDefinition = propertyDefinition;
	}
	
	PropertyDefinition getPropertyDefinition() {
		return propertyDefinition;
	}
	
	public ArooaClass getClassIdentifier() {
		return getPropertyDefinition().getPropertyType();
	}
	
    public void setProperty(String name, Object value) 
    throws ArooaPropertyException {
		throw new UnsupportedOperationException("Using the wrong setter for type.");    	
    }
    
    public void setMappedProperty(String name, String key, Object value) 
    throws ArooaPropertyException {
		throw new UnsupportedOperationException("Using the wrong setter for type.");    	
    }
    
    public void setIndexedProperty(String name, int index, Object value) 
    throws ArooaPropertyException {
		throw new UnsupportedOperationException("Using the wrong setter for type.");    	
    }
    
	public final void init() throws ArooaException {
		getParentContext().getRuntime().addRuntimeListener(runtimeListener);
	}
	
	public final void configure() throws ArooaException {
	}
	
    public final void destroy() { 
    	
    	// It's instances not containers that are destroyed, however this is 
    	// here for symmetry...
		getParentContext().getRuntime().removeRuntimeListener(runtimeListener);
    }
    
    /**
     * Convert if not a component property.
     * 
     * @param from
     * @return
     */
    Object convert(Object from) {
    	
    	if (getContext().getArooaType() == ArooaType.COMPONENT) {
    		return from;
    	}
		Class<?> propertyType = getClassIdentifier().forClass();
		ArooaConverter converter = getContext().getSession(
			).getTools().getArooaConverter();
		
		try {
			return converter.convert(from, 
					propertyType);
		} catch (NoConversionAvailableException e) {
			throw new ArooaPropertyException(
					getPropertyDefinition().getPropertyName(), e);
		} catch (ConversionFailedException e) {
			throw new ArooaPropertyException(
					getPropertyDefinition().getPropertyName(), e);
		}
    }
}
