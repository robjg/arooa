package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

/**
 * An {@link ArooaContext} for standard parsers.
 * 
 * @author rob
 *
 */
class StandardArooaContext implements ArooaContext {

	private final StandardRuntime runtime;
	private final ConfigurationNode runtimeNode;

	private final ArooaContext parent;
	
	private final ArooaType type;
	
	public StandardArooaContext(
			ArooaType type,
			StandardRuntime runtime, 
			ConfigurationNode runtimeNode,
			ArooaContext parent) {
		this.type = type;
		this.runtime = runtime;
		this.runtimeNode = runtimeNode;
		this.parent = parent;
	}
	
	public ArooaType getArooaType() {
		return type;
	}
	
	public ArooaContext getParent() {
		return parent;
	}
	
	public RuntimeConfiguration getRuntime() {
		return runtime;
	}
	
	public PrefixMappings getPrefixMappings() {
		return parent.getPrefixMappings();
	}
	
	public ArooaSession getSession() {
		return parent.getSession();
	}
	
	public ConfigurationNode getConfigurationNode() {
		return runtimeNode;
	}
	
	public ArooaHandler getArooaHandler() {
		return runtime.getHandler();
	}
	
}
