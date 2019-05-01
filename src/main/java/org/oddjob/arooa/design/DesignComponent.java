/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design;

/**
 * A DesignComponent provides the design configuration for a component.
 * 
 * @author rob
 */
public interface DesignComponent extends DesignInstance {
	
	/**
	 * Add a listener that will notified when child 
	 * {@link DesignComponent}s are added or removed from this 
	 * DesignComponent. This is used by the {@link org.oddjob.arooa.design.designer.DesignTreeModel}.
	 * 
	 * @param listener The listener.
	 */
	void addStructuralListener(DesignListener listener);

	/**
	 * Remove a listener.
	 * 
	 * @param listener The listener.
	 */
	void removeStructuralListener(DesignListener listener);
	
	/**
	 * Get the id of the instance, if there is one.
	 * 
	 * @return The id. May be null.
	 */
	String getId();
	
	/**
	 * Set the id of the instance.
	 * 
	 * @param id The id. May be null.
	 */
	void setId(String id);
}
