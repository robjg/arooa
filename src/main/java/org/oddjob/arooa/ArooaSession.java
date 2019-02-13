package org.oddjob.arooa;

import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.life.ComponentProxyResolver;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.runtime.PropertyManager;

/**
 * An ArooaSession encapsulates everything an {@link ArooaParser} needs 
 * to do it's job.
 * 
 * @author Rob Gordon.
 *
 */
public interface ArooaSession {

	/**
	 * Get the {@link ArooaDescriptor} to use.
	 *  
	 * @return An ArooaDescriptor. Must not be null.
	 */
    ArooaDescriptor getArooaDescriptor();

    /**
     * Get the underlying {@link ComponentPool}.
     * 
     * @return A ComponentPool. Must not be null.
     */
	ComponentPool getComponentPool();
	
    /**
     * Get the underlying {@link BeanRegistry}.
     * 
     * @return A BeanRegistry. Must not be null.
     */
	BeanRegistry getBeanRegistry();
	
	/**
	 * Get the {@link PropertyManager} for this session.
	 * 
	 * @return The PropertyMananger. Must not be null.
	 */
	PropertyManager getPropertyManager();
	
	/**
	 * Get the tools {@link ArooaTools} to use.
	 * 
	 * @return An instance of ArooaTools.
	 */
	ArooaTools getTools();

	/**
	 * Get the {@link ComponentPersister} to use.
	 * 
	 * @return A ComponentPersister. Can be null if
	 * no persistence is required.
	 */
	ComponentPersister getComponentPersister();
	
	/**
	 * 
	 * Get the {@link ComponentProxyResolver} to use.
	 * 
	 * @return A ComponentProxyResolver. Can be null if
	 * no Proxy substitution is required.
	 */
	ComponentProxyResolver getComponentProxyResolver();
	
}
