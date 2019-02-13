package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.parsing.ArooaContext;

/**
 * Encapsulates structure and values of a configuration. It must preserve
 * enough information so that it can also be an {@link ArooaConfiguration}.
 * 
 * @author rob
 *
 */
public interface ConfigurationNode extends ArooaConfiguration {

	/**
	 * Get the associated {@link ArooaContext} for this node. 
	 * 
	 * @return The ArooaContext. Never null.
	 */
	ArooaContext getContext();
	
	/**
	 * Add a listener to listen to configuration changes.
	 * 
	 * @param listener A listener.
	 */
	void addNodeListener(ConfigurationNodeListener listener);
	
	/**
	 * Remove a listener.
	 * 
	 * @param listener A listener.
	 */
	void removeNodeListener(ConfigurationNodeListener listener);
	
	/**
	 * Set the position for the next inserted node.
	 *  
	 * @param insertAt The position.
	 */
	void setInsertPosition(int insertAt);
	
	/**
	 * Insert a child in the parse Tree.
	 * 
	 * @param child  The child. Must not be null.
	 * 
	 * @return The insert position. 
	 */
	int insertChild(ConfigurationNode child);
	
	/**
	 * Remove a child.
	 * 
	 * @param index The index at which to remove the child.
	 */
	void removeChild(int index);
	
	/**
	 * Add text to the node.
	 * 
	 * @param text Some text.
	 */
	void addText(String text);
	
	/**
	 * Get the index of a child RuntimeNode. This is required for lists who's
	 * indexes may change as nodes are inserted and deleted. 
	 * 
	 * @param child The child node whose index is to be determined.
	 * 
	 * @return The index, or -1 if the node is not a child.
	 */
	int indexOf(ConfigurationNode child);
		
}