package org.oddjob.arooa.runtime;

import java.util.EventListener;

/**
 * Listen to changes in Configuration.
 * 
 * @author rob
 */
public interface ConfigurationNodeListener extends EventListener {

	/**
	 * A {@link ConfigurationNode} will make a request to all
	 * listeners before inserting a node.
	 * <p>
	 * An instance of this use is that a simple property will only 
	 * allow a single child.
	 * 
	 * @param nodeEvent The modification event.
     *
	 * @throws ModificationRefusedException If the listener veto's the request.
	 */
	void insertRequest(ConfigurationNodeEvent nodeEvent)
	throws ModificationRefusedException;
	
	/**
	 * A {@link ConfigurationNode} will make a request to all
	 * listeners before removing a node.
	 * 
	 * @param nodeEvent The modification event.
	 *
	 * @throws ModificationRefusedException If the listener veto's the request.
	 */
	void removalRequest(ConfigurationNodeEvent nodeEvent)
	throws ModificationRefusedException;
	
	/**
	 * Receive notification that a child has been inserted.
	 * 
	 * @param nodeEvent The modification event.
	 */
	void childInserted(ConfigurationNodeEvent nodeEvent);
	
	/**
	 * Receive notification that a child has been removed.
	 * 
	 * @param nodeEvent The modification event.
	 */
	void childRemoved(ConfigurationNodeEvent nodeEvent);
	
}
