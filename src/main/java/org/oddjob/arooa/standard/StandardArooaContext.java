package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

/**
 * An {@link ArooaContext} for standard parsers. This is created for
 * both components and values.
 * 
 * @author rob
 *
 */
class StandardArooaContext implements ArooaContext {

	private final StandardRuntime runtime;

	private final ConfigurationNode configurationNode;

	private final ArooaContext parent;
	
	private final ArooaType type;

    /**
     * Constructor.
     *
     * @param type The type of object or property
     * @param runtime The runtime to be provided by this context.
     * @param configurationNode The configuration node to be provided by
     *                          this context.
     * @param parent The parent context.
     */
	public StandardArooaContext(
			ArooaType type,
			StandardRuntime runtime, 
			ConfigurationNode configurationNode,
			ArooaContext parent) {
		this.type = type;
		this.runtime = runtime;
		this.configurationNode = configurationNode;
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
		return configurationNode;
	}
	
	public ArooaHandler getArooaHandler() {
		return runtime.getHandler();
	}
	
}
