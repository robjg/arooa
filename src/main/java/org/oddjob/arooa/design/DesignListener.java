package org.oddjob.arooa.design;

/**
 * A class which implements this interface is able to receive structural events.
 * 
 * @author Rob Gordon.
 */

public interface DesignListener {

	/**
	 * Called when a child is added to a Structural object.
	 * 
	 * @param event The structural event.
	 */
	public void childAdded(DesignStructureEvent event);
	
	/**
	 * Called when a child is removed from a Structural object.
	 * 
	 * @param event The structural event.
	 */
	public void childRemoved(DesignStructureEvent event);
}
