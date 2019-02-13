package org.oddjob.arooa.parsing;

/**
 * Listen to {@link ConfigurationOwner} state changes.
 *  
 * @author rob
 *
 */
public interface OwnerStateListener {

	/**
	 * Called when the {@link ConfigurationOwner}s session
	 * changes. Either when a new one is created or 
	 * an existing one is destroyed.
	 * <p/>
	 * This happens, for instance, when Oddjob is loaded and reset.
     *
	 * @param event The event.
	 */
	void sessionChanged(ConfigOwnerEvent event);
}
