package org.oddjob.arooa.parsing;

import java.util.EventListener;

/**
 * Listen to {@link ConfigurationSession} state changes.
 *  
 * @author rob
 *
 */
public interface SessionStateListener extends EventListener {

	/**
	 * Called when the session is modified.
	 * 
	 * @param event The event.
	 */
	void sessionModified(ConfigSessionEvent event);
	
	/**
	 * Called when the session is saved.
	 * 
	 * @param event The event.
	 */
	void sessionSaved(ConfigSessionEvent event);
}
