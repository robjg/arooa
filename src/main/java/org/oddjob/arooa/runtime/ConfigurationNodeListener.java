package org.oddjob.arooa.runtime;

import org.oddjob.arooa.parsing.ParseContext;

import java.util.EventListener;

/**
 * Listen to changes in Configuration.
 * 
 * @author rob
 */
public interface ConfigurationNodeListener<P extends ParseContext<P>> extends EventListener {

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
	void insertRequest(ConfigurationNodeEvent<P> nodeEvent)
	throws ModificationRefusedException;
	
	/**
	 * A {@link ConfigurationNode} will make a request to all
	 * listeners before removing a node.
	 * 
	 * @param nodeEvent The modification event.
	 *
	 * @throws ModificationRefusedException If the listener veto's the request.
	 */
	void removalRequest(ConfigurationNodeEvent<P> nodeEvent)
	throws ModificationRefusedException;
	
	/**
	 * Receive notification that a child has been inserted.
	 * 
	 * @param nodeEvent The modification event.
	 */
	void childInserted(ConfigurationNodeEvent<P> nodeEvent);
	
	/**
	 * Receive notification that a child has been removed.
	 * 
	 * @param nodeEvent The modification event.
	 */
	void childRemoved(ConfigurationNodeEvent<P> nodeEvent);
	
}
