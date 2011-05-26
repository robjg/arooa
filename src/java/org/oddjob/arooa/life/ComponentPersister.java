package org.oddjob.arooa.life;

import org.oddjob.arooa.ArooaSession;

/**
 * Something capable of persisting a component. The Object passed is the
 * proxy not the underlying component.
 * 
 * @author rob
 *
 */
public interface ComponentPersister {

	/**
	 * Persist the proxy Object.
	 * <p>
	 * The id is given although it could be discovered from the ComponentPool
	 * in the ArooaSession. This is for symmetry with the other methods of this
	 * interface.
	 * 
	 * @param id The id for proxy.
	 * @param proxy The object to persist.
	 * @param session The session.
	 */
	public void persist(String id, Object proxy, ArooaSession session)
	throws ComponentPersistException;

	/**
	 * Restore the proxy Object.
	 * 
	 * @param id The id.
	 * @param session The session.
	 * 
	 * @return The restored object.
	 */
	public Object restore(String id, ClassLoader classLoader, ArooaSession session)
	throws ComponentPersistException;

	/**
	 * Remove an object from the store.
	 * 
	 * @param id
	 * @param session
	 */
	public void remove(String id, ArooaSession session)
	throws ComponentPersistException;
			
	/**
	 * List the persisted components.
	 * 
	 * @return The ids of the persisted components.
	 */
	public String[] list()
	throws ComponentPersistException;

	/**
	 * Clear this persister of everything stored.
	 * 
	 */
	public void clear()
	throws ComponentPersistException;	
		
	/**
	 * Free resources used by the implementation.
	 */
	public void close();
}
