package org.oddjob.arooa.standard;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.life.ComponentPersistException;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;

/**
 * Component configuration behaviour. Components are self 
 * configuring. They
 * should ignore requests to configure when the parent is
 * being configured.
 * 
 * @author rob
 *
 */
class ComponentConfiguration extends InstanceConfiguration {
	private static final Logger logger = Logger.getLogger(
			ComponentConfiguration.class);

	private final InjectionStrategy injectionStrategy = new InjectionStrategy() {
		
		public void init(ParentPropertySetter parentPropertySetter) 
		throws ArooaPropertyException {
			parentPropertySetter.parentSetProperty(
					getObjectToSet());
		}
		
		public void configure(ParentPropertySetter parentPropertySetter) {
		}

		public void destroy(ParentPropertySetter parentPropertySetter) 
		throws ArooaPropertyException {
				parentPropertySetter.parentSetProperty(
						null);
		}		
	};
	
	private final Object proxy;

	
	public ComponentConfiguration(
			ArooaClass arooaClass,
			Object wrappedObject, 
			Object proxy,
			ArooaAttributes attributes) {
		super(arooaClass, wrappedObject, attributes);
		this.proxy = proxy;	
	}

	@Override
	Object getObjectToSet() {
		return proxy;
	}
	
    @Override
    InjectionStrategy injectionStrategy() {
    	return injectionStrategy;
    }
	
	@Override
	void init(InstanceRuntime ourWrapper,
			ArooaContext context) 
	throws ArooaConfigurationException {
    	ourWrapper.fireBeforeInit();

    	// order is important. A component must be registered before it is
    	// added to it's parent - a parent might want to use it's id.
    	
		context.getSession().getComponentPool().registerComponent(
				new ComponentTrinity(
						getWrappedObject(), proxy, context), 
				getId());
		
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
    throws ArooaException {
    	// don't listen to parents
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
			ArooaContext context) 
	throws ArooaConfigurationException {
		
    	ourWrapper.fireBeforeDestroy();
    	
		try {
			context.getSession().getComponentPool().remove(
							proxy);
		} catch (ComponentPersistException e) {
			logger.warn("Failed removing component " + proxy + 
					" from pool.", e);
		}
		
		injectionStrategy().destroy(ourWrapper.getParentPropertySetter());
		
    	ourWrapper.fireAfterDestroy();    	
	}
}
