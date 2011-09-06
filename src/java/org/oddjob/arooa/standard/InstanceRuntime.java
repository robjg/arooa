package org.oddjob.arooa.standard;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListener;

/**
 * {@link RuntimeConfiguration} for an instance of something (a 
 * component or value).
 * 
 * @author rob
 */
abstract class InstanceRuntime extends StandardRuntime 
implements RuntimeConfiguration {		
	
	private final Map<ArooaElement, ArooaContext> childContexts =
		new HashMap<ArooaElement, ArooaContext>();
	
	private final InstanceConfiguration instance;

	private final ArooaClass runtimeClass;
	
	/**
	 * Added to a parent runtime to ensure configuration
	 * and destruction events are passed down the hierarchy.
	 */
	private final RuntimeListener runtimeListener = 
		new RuntimeListener() {

		public void beforeInit(RuntimeEvent event) 
		throws ArooaConfigurationException {
		}
		
		public void afterInit(RuntimeEvent event) 
		throws ArooaConfigurationException {			
		}
		
		public void beforeConfigure(RuntimeEvent event) 
		throws ArooaConfigurationException {
			getInstance().listenerConfigure(
					InstanceRuntime.this, 
					getContext());
		}
		public void afterConfigure(RuntimeEvent event)
		throws ArooaConfigurationException {
		}
		
		public void beforeDestroy(RuntimeEvent event) 
		throws ArooaConfigurationException {
			getInstance().listenerDestroy(InstanceRuntime.this, 
					getContext());
			
			int index = getParentContext().getConfigurationNode().indexOf(
					getContext().getConfigurationNode());
			if (index < 0) {
				throw new IllegalStateException(
				"Configuration node is not a child of the parent node.");
			}

			getParentContext().getConfigurationNode().removeChild(
					index);
			
			getParentContext().getRuntime().removeRuntimeListener(runtimeListener);
		}
		
		public void afterDestroy(RuntimeEvent event) 
		throws ArooaConfigurationException {
		}
	};
	
	/** True when init has completed without an exception. */
	private boolean fullyInitialised;
	
	public InstanceRuntime(
			InstanceConfiguration instance,
			ArooaContext parentContext) {
		super(parentContext);

		if (instance == null) {
			throw new NullPointerException("No Instance.");
		}
		
		this.instance = instance;
		this.runtimeClass = instance.getArooaClass();
		
		if (runtimeClass == null) {
			throw new NullPointerException("No Arooa Class.");
		}		
	}

	InstanceConfiguration getInstance() {
		return instance;
	}

	void setContext(ArooaContext suggestedContext) 
	throws ArooaConfigurationException {
	
		ArooaDescriptor descriptor = suggestedContext.getSession(
				).getArooaDescriptor();

		PropertyAccessor accessor = suggestedContext.getSession(
				).getTools().getPropertyAccessor();
		
		ArooaBeanDescriptor beanDescriptor = descriptor.getBeanDescriptor(
				getClassIdentifier(), accessor);

		ArooaContext context = suggestedContext;
		
		if (beanDescriptor != null) {
			ParsingInterceptor interceptor = beanDescriptor.getParsingInterceptor();

			if (interceptor != null) {
				context = interceptor.intercept(context);
			}
		}
			
		// Context.getRuntime and this Rutime might be different by now.
		new LifecycleObligations().honour(
				InstanceRuntime.this, context);
		
		super.setContext(context);
	}
	
	@Override
	ArooaHandler getHandler() {

		return new ArooaHandler() {
		
			public ArooaContext onStartElement(
					final ArooaElement element, ArooaContext parentContext) 
			throws ArooaConfigurationException {

				ArooaContext propertyContext = childContexts.get(element);
				
				if (propertyContext == null) {
				
		    		PropertyHandler propertyHandler = new PropertyHandler();
		    		propertyContext = propertyHandler.onStartElement(
		    				element, parentContext);
		    		
		    		childContexts.put(element, propertyContext);
				}
		    		
				return propertyContext;
			}
		};
	}
	

	public ArooaClass getClassIdentifier() {
		return runtimeClass;
	}
	
	abstract ParentPropertySetter getParentPropertySetter();
	
	public void init() throws ArooaConfigurationException {
		getInstance().init(
				this,
				getContext());
		
		// Root instance doesn't have a parent runtime.
		if (getParentContext().getRuntime() != null) { 
			getParentContext().getRuntime().addRuntimeListener(runtimeListener);
		}
		
		// initialisation might have thrown an exception. 
		fullyInitialised = true;
	}
	
	public void configure() throws ArooaConfigurationException {
		getInstance().configure(
				this,
				getContext());
		
	}
	
	public void destroy() throws ArooaConfigurationException {
		
		// If this is destroyed directly (by a cut) then remove listener.
		if (getParentContext().getRuntime() != null) { 
			getParentContext().getRuntime().removeRuntimeListener(runtimeListener);
		}
		
		// if initialisation throws an exception.
		if (fullyInitialised) {
			getInstance().destroy(
					this,
					getContext());
		}
	}

	public void setProperty(String name, Object value) 
	throws ArooaPropertyException {
		instance.setProperty(name, value, getContext());
	}

	public void setIndexedProperty(String name, int index, Object value) 
	throws ArooaPropertyException {
		instance.setIndexedProperty(name, index, value, getContext());
	}

	public void setMappedProperty(String name, String key, Object value) 
	throws ArooaPropertyException {
		instance.setMappedProperty(name, key, value, getContext());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " for " + instance.getWrappedObject();
	}
}
