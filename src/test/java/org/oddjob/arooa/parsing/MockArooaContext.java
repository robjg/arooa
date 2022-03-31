package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

public class MockArooaContext implements ArooaContext {

	@Override
	public ArooaType getArooaType() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}

	@Override
	public ArooaContext getParent() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}

	@Override
	public PrefixMappings getPrefixMappings() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}

	@Override
	public RuntimeConfiguration getRuntime() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}

	@Override
	public ArooaSession getSession() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}

	@Override
	public ConfigurationNode<ArooaContext> getConfigurationNode() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}

	@Override
	public ArooaHandler getArooaHandler() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}
	
}
