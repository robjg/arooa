package org.oddjob.arooa.registry;

/**
 * Finds services for automatically setting on bean instances.
 * 
 * @author rob
 *
 */
public interface ServiceFinder {

	/**
	 * Find a service. Why isn't this parameterised?
	 * 
	 * @param cl The class of the service.
	 * @param flavour The flavour of the service. Not yet implemented.
	 * 
	 * @return
	 */
	public Object find(Class<?> cl, String flavour);

}
