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
	 * @return A {@link ConfigurationSession}. My be null if no session is available.
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
	
	/**
	 * Get the design factory for the configuration. If this is null
	 * the Oddjob Explorer won't show a DesignInside action.
	 * 
	 * @return A DesignFactory. Must not be null if a 
	 * ConfigurationSession is available.
	 */
	public DesignFactory rootDesignFactory();
	
	/**
	 * Get the root element.
	 * 
	 * @return The root element of the configuration. Must not be null if
	 * a ConfiguraitonSession is available.
	 */
	public ArooaElement rootElement();
	
}
