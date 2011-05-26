package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.ConfigurationNode;

public class MockArooaContext implements ArooaContext {

	public ArooaType getArooaType() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}
	
	public ArooaContext getParent() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}
	
	public PrefixMappings getPrefixMappings() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}

	public RuntimeConfiguration getRuntime() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}

	public ArooaSession getSession() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}

	public ConfigurationNode getConfigurationNode() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}
	
	public ArooaHandler getArooaHandler() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}
	
}
