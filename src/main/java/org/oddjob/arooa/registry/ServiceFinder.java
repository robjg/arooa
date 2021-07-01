package org.oddjob.arooa.registry;

/**
 * Finds services for automatically setting on bean instances.
 * 
 * @author rob
 *
 */
public interface ServiceFinder {

	/**
	 * Find a service.
	 * 
	 * @param cl The class of the service.
	 * @param qualifier The flavour of the service. Not yet implemented.
	 * 
	 * @return
	 */
	<T> T find(Class<T> cl, String qualifier);

}
