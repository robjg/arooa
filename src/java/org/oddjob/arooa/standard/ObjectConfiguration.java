package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;


/**
 * 
 */
class ObjectConfiguration extends InstanceConfiguration {

	private final InjectionStrategy injectionStrategy = new InjectionStrategy() {
		boolean configured;

		public void init(ParentPropertySetter parentPropertySetter) {
		}
		
		public void configure(ParentPropertySetter parentPropertySetter) 
		throws ArooaPropertyException {
			if (configured) {
				parentPropertySetter.parentSetProperty(
						null);
			}
			parentPropertySetter.parentSetProperty(
					getObjectToSet());
			configured = true;
		}

		public void destroy(ParentPropertySetter parentPropertySetter) 
		throws ArooaPropertyException {
			if (configured) {
				parentPropertySetter.parentSetProperty(
						null);
			}
		}		
	};
	
    /**
     * Constructor for creating a wrapper for the specified object.
     * <p>
     * 
     * @param wrappedObject The element to configure. Must not be <code>null</code>.
     */
    public ObjectConfiguration(ArooaClass arooaClass, 
    		Object wrappedObject, ArooaAttributes attributes) {
    	super(arooaClass, wrappedObject, attributes);
    }
    
    @Override
    InjectionStrategy injectionStrategy() {
    	return injectionStrategy;
    }
    
    @Override
    Object getObjectToSet() {
    	return getWrappedObject();
    }
    
    @Override
    void init(InstanceRuntime ourWrapper,
    		ArooaContext context) 
    throws ArooaConfigurationException {
    	ourWrapper.fireBeforeInit();
    	// order is important. A component must be registered before it is
    	// added to it's parent - a parent might want to use it's id.

    	String id = getId();
    	if (id != null) {
    		context.getSession().getBeanRegistry().register(
    				id, getWrappedObject());
    	}
		
    	internalInit(context);
    	
    	ourWrapper.fireAfterInit();
    	
		injectionStrategy().init(ourWrapper.getParentPropertySetter());
    }
    
    @Override
    void configure(InstanceRuntime ourWrapper,
    		ArooaContext context) 
    throws ArooaConfigurationException {
    	ourWrapper.fireBeforeConfigure();
    	
    	internalConfigure(context);
    	
    	ourWrapper.fireAfterConfigure();
    	
		injectionStrategy().configure(ourWrapper.getParentPropertySetter());
    }
    
    @Override
    void listenerConfigure(InstanceRuntime ourWrapper,
    		ArooaContext context) 
    throws ArooaConfigurationException {    	
    	ourWrapper.fireBeforeConfigure();
    	
    	internalConfigure(context);
    	
    	ourWrapper.fireAfterConfigure();
		
		injectionStrategy().configure(ourWrapper.getParentPropertySetter());
    }

	@Override
	void destroy(InstanceRuntime ourWrapper,
			ArooaContext context) 
	throws ArooaConfigurationException {
		doDestroy(ourWrapper, context);
	}
	
	@Override
    void listenerDestroy(InstanceRuntime ourWrapper,
    		ArooaContext context) 
    throws ArooaConfigurationException {
		doDestroy(ourWrapper, context);
    }

	private void doDestroy(InstanceRuntime ourWrapper,
    		ArooaContext context) {
    	ourWrapper.fireBeforeDestroy();
    	
    	context.getSession().getBeanRegistry().remove(
    			getWrappedObject());
    	
    	internalDestroy(context);
    	
		injectionStrategy().destroy(ourWrapper.getParentPropertySetter());
		
    	ourWrapper.fireAfterDestroy();		    	
	}
}
