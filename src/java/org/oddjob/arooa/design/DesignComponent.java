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
	
}
