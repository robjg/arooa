package org.oddjob.arooa.design;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.runtime.AbstractRuntimeConfiguration;

abstract class DesignRuntime extends AbstractRuntimeConfiguration {
	
	public void init() throws ArooaConfigurationException {
		fireBeforeInit();
		fireAfterInit();
	}
	
	public void configure() throws ArooaConfigurationException {
		fireBeforeConfigure();
		fireAfterConfigure();
	}
	
	public void destroy() throws ArooaConfigurationException {
		fireBeforeDestroy();
		fireAfterDestroy();
	}
	
	public ArooaClass getClassIdentifier() {
		throw new UnsupportedOperationException();
	}
	
}
