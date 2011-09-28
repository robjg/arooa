/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design;


/**
 * A DesignComponent provides the configuration for a job.
 * 
 * 
 */
public interface DesignComponent extends DesignInstance {
	
	public void addStructuralListener(DesignListener listener);
	
	public void removeStructuralListener(DesignListener listener);
	
	/**
	 * Get the id of the instance, if there is one.
	 * 
	 * @return The id. May be null.
	 */
	public String getId();
	
	/**
	 * Set the id of the instance.
	 * 
	 * @param id The id. May be null.
	 */
	public void setId(String id);
}
