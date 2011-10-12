package org.oddjob.arooa.design;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

/**
 * Used when creating a context for a design because no {@link ArooaHandler}
 * is needed.
 * 
 * @author rob
 *
 */
public class DesignSeedContext implements ArooaContext {

	private final ArooaSession session;
	
	private final ArooaType type;
	
	private final PrefixMappings prefixMappings = 
		new SimplePrefixMappings();
	
	public DesignSeedContext(ArooaType type, ArooaSession session) {
		this.type = type;
		this.session = session;
	}

	public ArooaType getArooaType() {
		return type;
	}
	
	public ArooaContext getParent() {
		return null;
	}
	
	public ArooaHandler getArooaHandler() {
		throw new UnsupportedOperationException();
	}
	
	public ConfigurationNode getConfigurationNode() {
		return null;
	}
	
	public PrefixMappings getPrefixMappings() {
		return prefixMappings;
	}
	
	public RuntimeConfiguration getRuntime() {
		return null;
	}
	
	public ArooaSession getSession() {
		return session;
	}
}
