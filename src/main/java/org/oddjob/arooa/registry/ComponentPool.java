package org.oddjob.arooa.registry;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.life.ComponentPersistException;
import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.parsing.ArooaContext;

/**
 * A ComponentPool provides access to the Components created by parsing
 * an {@link ArooaConfiguration}.
 * 
 * @author rob
 *
 */
public interface ComponentPool {

	/**
	 * Configure the component. The is a shortcut method
	 * equivalent to 
	 * <code>contextFor(component).getRuntime().configure().</code>
	 * 
	 * @param component The component or it's proxy.
	 */
	void configure(Object component)
	throws ArooaConfigurationException;

	/**
	 * Possibly save the component to a persistent store, if everything
	 * is in place for this to happen. 
	 * <p>
	 * If a {@link ComponentPersister} is in operation and the component
	 * was registered with an id then the ComponentPersister is 
	 * invoked with the component. 
	 * 
	 * @param either A component or it's proxy.
	 */
	void save(Object either) throws ComponentPersistException;
	
	/**
	 * Remove a component.
	 * 
	 * @param either A component or it's proxy.
     *
     * @return true if the component was removed.
	 */
	boolean remove(Object either) throws ComponentPersistException;
	
	/**
	 * Get the components {@link ArooaContext}.
	 * 
	 * @param either The component or the proxy.
	 * 
	 * @return The context or null if the component/proxy is not 
	 * in this pool.
	 */
	ArooaContext contextFor(Object either);
	
	/**
	 * Get the {@link ComponentTrinity} for the given {@link ArooaContext}.
	 * <p>
	 * This is required for {@link ContextHierarchyServiceFinder}.
	 * 
	 * @param context A context;
	 * 
	 * @return The trinity or null.
	 * 
	 * @since 1.3
	 */
	ComponentTrinity trinityForContext(ArooaContext context);
	
	/**
	 * Get the {@link ComponentTrinity} for either the component or 
	 * proxy.
	 * <p>
	 * 
	 * @param either Either the component or the proxy.
	 * 
	 * @return The component trinity or null if it can't be found.
	 * 
	 * @since 1.3
	 * 
	 */
	ComponentTrinity trinityFor(Object either);
	
	/**
	 * Register a {@link ComponentTrinity}.
	 * 
	 * @param trinity The trinity.
	 * @param id The id. May be null.
	 *
     * @return The new id if one was assigned.
	 */
	String registerComponent(ComponentTrinity trinity, String id);
	
	/**
	 * Provide the Id for either a component or it's proxy.
	 * 
	 * @param either Either a component or it's proxy.
	 * 
	 * @return The id, or null if the component wasn't registered with
	 * an id, or the component doesn't exist in pool.
	 */
	String getIdFor(Object either);
	
	/**
	 * Provide the {@link ComponentTrinity} for a given id, if possible.
	 * 
	 * @param id The id.
	 * 
	 * @return The trinity, or null if none exists for the given id.
	 */
	ComponentTrinity trinityForId(String id);
	
	/**
	 * Provide a way of iterating over all trinities.
	 * 
	 * @return An Iterable of trinities. Will never be null.
	 */
	Iterable<ComponentTrinity> allTrinities();
	
}
