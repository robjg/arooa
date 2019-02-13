package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;


/**
 * Configuration for beans that aren't components.
 *
 * @see ComponentConfiguration
 *
 * @author rob
 *
 */
class ObjectConfiguration extends InstanceConfiguration {

    private final AttributeSetter attributeSetter;

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
     * @param arooaClass The arooa type of the object.
     * @param wrappedObject The element to configure. Must not be <code>null</code>.
     * @param attributes The attributes.
     */
    public ObjectConfiguration(ArooaClass arooaClass, 
    		Object wrappedObject, ArooaAttributes attributes) {
    	super(arooaClass, wrappedObject);

        this.attributeSetter = new AttributeSetter(this,
                                                   attributes);
    }

    @Override
    AttributeSetter getAttributeSetter() {
        return attributeSetter;
    }

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
