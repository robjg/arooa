package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.life.ComponentProxyResolver;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.runtime.PropertyManager;

/**
 * Designed to be partially overridden to modify behaviour.
 * 
 * @author rob
 *
 */
public class SessionDelegate implements ArooaSession {

	private final ArooaSession delegate;
	
	/**
	 * Constructor.
	 * 
	 * @param delegate Existing session.
	 */
	public SessionDelegate(ArooaSession delegate) {
		if (delegate == null) {
			throw new NullPointerException("No delegate session.");
		}
		this.delegate = delegate;
	}

	@Override
	public ComponentPool getComponentPool() {
		return delegate.getComponentPool();
	}
	
	@Override
	public BeanRegistry getBeanRegistry() {
		return delegate.getBeanRegistry();
	}

	@Override
	public PropertyManager getPropertyManager() {
		return delegate.getPropertyManager();
	}
	
	@Override
	public ArooaDescriptor getArooaDescriptor() {
		return delegate.getArooaDescriptor();
	}

	@Override
	public ArooaTools getTools() {
		return delegate.getTools();
	}

	@Override
	public ComponentProxyResolver getComponentProxyResolver() {
		return delegate.getComponentProxyResolver();
	}
	
	@Override
	public ComponentPersister getComponentPersister() {
		return delegate.getComponentPersister();
	}
	
	/**
	 * Getter for the original.
	 * 
	 * @return The original session this instance is delegating to.
	 */
	public ArooaSession getOriginal() {
		return delegate;
	}
}
