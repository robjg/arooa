package org.oddjob.arooa.parsing.interceptors;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.life.ComponentProxyResolver;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.runtime.PropertyManager;

public class DescriptorOverrideSession implements ArooaSession {

	private final ArooaSession override;
	private final ArooaDescriptor descriptor;
	
	public DescriptorOverrideSession(
			ArooaSession override, ArooaDescriptor descriptor) {
		this.override = override;
		this.descriptor = descriptor;
	}

	@Override
	public ComponentPool getComponentPool() {
		return override.getComponentPool();
	}
	
	@Override
	public BeanRegistry getBeanRegistry() {
		return override.getBeanRegistry();
	}

	@Override
	public PropertyManager getPropertyManager() {
		return override.getPropertyManager();
	}
	
	@Override
	public ArooaDescriptor getArooaDescriptor() {
		return descriptor;
	}

	@Override
	public ArooaTools getTools() {
		return override.getTools();
	}

	@Override
	public ComponentProxyResolver getComponentProxyResolver() {
		return override.getComponentProxyResolver();
	}
	
	@Override
	public ComponentPersister getComponentPersister() {
		return override.getComponentPersister();
	}
}
