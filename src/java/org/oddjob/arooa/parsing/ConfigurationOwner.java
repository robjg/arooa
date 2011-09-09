package org.oddjob.arooa.parsing;

import org.oddjob.arooa.design.DesignFactory;


/**
 * Something, typically  a component, that is able to provide editing
 * facilities for an ArooaConfiguration.
 * 
 * @author rob
 *
 */
public interface ConfigurationOwner {

	/**
	 * Provide a {@link ConfigurationSession}.
	 * 
	 * @return A {@link ConfigurationSession}. Never Null.
	 */
	public ConfigurationSession provideConfigurationSession();
	
	/**
	 * Add a listener.
	 * 
	 * @param listener
	 */
	public void addOwnerStateListener(OwnerStateListener listener);
	
	/**
	 * Remove a listener.
	 * 
	 * @param listener
	 */
	public void removeOwnerStateListener(OwnerStateListener listener);
	
	
	public DesignFactory rootDesignFactory();
	
	public ArooaElement rootElement();
	
}
