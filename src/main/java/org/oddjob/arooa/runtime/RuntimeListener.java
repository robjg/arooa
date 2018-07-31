package org.oddjob.arooa.runtime;

import java.util.EventListener;

import org.oddjob.arooa.ArooaConfigurationException;

/**
 * Able to listen to configuration events from {@link RuntimeConfiguration}s.
 * <p>
 * Implementations will mainly be child RuntimeConfigurations that wish
 * to know when there parents are being configured.
 * 
 * @author rob
 *
 */
public interface RuntimeListener extends EventListener {

	public void beforeInit(RuntimeEvent event) 
	throws ArooaConfigurationException;
	
	public void afterInit(RuntimeEvent event) 
	throws ArooaConfigurationException;
	
	public void beforeConfigure(RuntimeEvent event) 
	throws ArooaConfigurationException;

	public void afterConfigure(RuntimeEvent event) 
	throws ArooaConfigurationException;
	
	public void beforeDestroy(RuntimeEvent event) 
	throws ArooaConfigurationException;
	
	public void afterDestroy(RuntimeEvent event) 
	throws ArooaConfigurationException;
}
