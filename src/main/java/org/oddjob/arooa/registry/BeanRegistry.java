/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.registry;


/**
 * Register components by id and look them up. 
 * 
 * @author Rob Gordon.
 */
public interface BeanRegistry extends BeanDirectory {

	
    /**
     * Register an object. The id should not contain reserved characters.
     * 
     * @param id The id of the object.
     * @param component The object.
     */
	void register(String id, Object component)
	throws InvalidIdException;

	
	/**
	 * Remove a component from the registry if it exists.
	 * 
	 * @param component The component.
	 */
	void remove(Object component);

}
