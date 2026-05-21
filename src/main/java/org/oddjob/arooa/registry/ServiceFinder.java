package org.oddjob.arooa.registry;

import java.lang.reflect.Type;

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
	 * @return A service or null.
	 */
	<T> T find(Type cl, String qualifier);

}
