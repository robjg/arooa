package org.oddjob.arooa;

import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.life.ComponentProxyResolver;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.runtime.PropertyManager;

public class MockArooaSession implements ArooaSession {
	
	@Override
	public ComponentProxyResolver getComponentProxyResolver() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public ComponentPersister getComponentPersister() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public ComponentPool getComponentPool() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public BeanRegistry getBeanRegistry() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public PropertyManager getPropertyManager() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public ArooaDescriptor getArooaDescriptor() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public ArooaTools getTools() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
}
