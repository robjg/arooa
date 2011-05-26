package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.ConfigurationNode;


public class HandlerOverrideContext implements ArooaContext {

	private final ArooaContext existingContext;
	
	private final ArooaHandler handlerOverride;

	public HandlerOverrideContext(ArooaContext context, 
			ArooaHandler handlerOverride) {
		this.existingContext = context;
		this.handlerOverride = handlerOverride;
	}
	
	public ArooaType getArooaType() {
		return existingContext.getArooaType();
	}
	
	public ArooaContext getParent() {
		return existingContext.getParent();
	}
	
	public ArooaHandler getArooaHandler() {
		return handlerOverride;
	}
	
	public RuntimeConfiguration getRuntime() {
		return existingContext.getRuntime();
	}
	
	public PrefixMappings getPrefixMappings() {
		return existingContext.getPrefixMappings();
	}
	
	public ConfigurationNode getConfigurationNode() {
		return existingContext.getConfigurationNode();
	}
	
	public ArooaSession getSession() {
		return existingContext.getSession();
	}
	
}
