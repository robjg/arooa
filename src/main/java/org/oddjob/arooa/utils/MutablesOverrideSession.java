package org.oddjob.arooa.utils;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.parsing.SessionDelegate;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.registry.SimpleBeanRegistry;
import org.oddjob.arooa.registry.SimpleComponentPool;
import org.oddjob.arooa.runtime.PropertyManager;
import org.oddjob.arooa.standard.StandardPropertyManager;

/**
 * A version of an {@link ArooaSession} that creates it's own copy
 * of a {@link BeanRegistry}, {@link ComponentPool} and {@link PropertyManager}.
 * 
 * @author rob
 *
 */
public class MutablesOverrideSession extends SessionDelegate
implements ArooaSession {

	private final ComponentPool componentPool;

	private final BeanRegistry beanRegistry;

	private final PropertyManager propertyManager;

	/**
	 * Constructor.
	 *
	 * @param original The original session.
	 */
	public MutablesOverrideSession(ArooaSession original) {
		super(original);

		this.componentPool = new SimpleComponentPool();

		this.beanRegistry = new SimpleBeanRegistry(
				original.getTools().getPropertyAccessor(),
				original.getTools().getArooaConverter());

		this.propertyManager = new StandardPropertyManager(
				original.getPropertyManager());
	}
		
	@Override
	public BeanRegistry getBeanRegistry() {
		return beanRegistry;
	}

	@Override
	public ComponentPool getComponentPool() {
		return componentPool;
	}

	@Override
	public PropertyManager getPropertyManager() {
		return propertyManager;
	}

}
