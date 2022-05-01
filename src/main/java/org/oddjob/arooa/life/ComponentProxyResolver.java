package org.oddjob.arooa.life;

import org.oddjob.arooa.ArooaSession;

/**
 * Used to resolve an object into a proxy for that object. This allows
 * Oddjob to wrap a Runnable as something with State and an Icon - amongst 
 * other things.
 * 
 * @author rob
 *
 */
public interface ComponentProxyResolver  {

	/**
	 * Possibly create a proxy for a given component.
	 * 
	 * @param object The object to possibly proxy.
	 * @param session A session.
	 * 
	 * @return The proxy, or the original object.
	 */
	Object resolve(Object object, ArooaSession session);
	
	/**
	 * Restore what is possibly a proxy to provide the original component.
	 * 
	 * @param proxy Possbily the proxy.
	 * @param session A session.
	 * 
	 * @return The component being proxied.
	 */
	Object restore(Object proxy, ArooaSession session);
}
