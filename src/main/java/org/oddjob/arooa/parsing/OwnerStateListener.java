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
	 * 
	 * @param event
	 */
	public void sessionChanged(ConfigOwnerEvent event);
}
