package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.runtime.AbstractRuntimeConfiguration;

abstract class StandardRuntime extends AbstractRuntimeConfiguration {

	private final ArooaContext parentContext;
	
	private ArooaContext context;
	
	StandardRuntime(ArooaContext parentContext) {
		this.parentContext = parentContext;
	}
	
	protected void fireBeforeInit() 
	throws ArooaConfigurationException {
		super.fireBeforeInit();
	}
		
	protected void fireAfterInit() 
	throws ArooaConfigurationException {
		super.fireAfterInit();
	}
	
	protected void fireBeforeConfigure() 
	throws ArooaConfigurationException {
		super.fireBeforeConfigure();
	}
	
	protected void fireAfterConfigure() 
	throws ArooaConfigurationException {
		super.fireAfterConfigure();
	}
	
	protected void fireBeforeDestroy() 
	throws ArooaConfigurationException {
		super.fireBeforeDestroy();
	}

	protected void fireAfterDestroy() 
	throws ArooaConfigurationException {
		super.fireAfterDestroy();
	}

	abstract ArooaHandler getHandler();
		
	void setContext(ArooaContext context) 
	throws ArooaConfigurationException {
		this.context = context;
	}
	
	ArooaContext getContext() {
		return context;
	}

	ArooaContext getParentContext() {
		return parentContext;
	}	
}
