package org.oddjob.arooa.design;

/**
 * Something that is able to notify listeners of changes to the design. 
 * 
 * @author rob
 *
 */
public interface DesignNotifier {

	/**
	 * Add a listener. Before this method returns the listener will 
	 * have received the events required to match the state of the
	 * design.
	 * 
	 * @param listener The listener.
	 * 
	 */
	void addDesignListener(DesignListener listener);
	
	/**
	 * Remove a listener.
	 * 
	 * @param listener The listener.
	 */
	void removeDesignListener(DesignListener listener);

}
