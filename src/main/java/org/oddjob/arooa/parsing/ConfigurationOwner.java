package org.oddjob.arooa.parsing;



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
	ConfigurationSession provideConfigurationSession();
	
	/**
	 * Add a listener.
	 * 
	 * @param listener
	 */
	void addOwnerStateListener(OwnerStateListener listener);
	
	/**
	 * Remove a listener.
	 * 
	 * @param listener
	 */
	void removeOwnerStateListener(OwnerStateListener listener);
	
	/**
	 * Get the design factory for the configuration. If this is null
	 * the Oddjob Explorer won't show a DesignInside action.
	 * <p>
	 * Note that this is a {@link SerializableDesignFactory} so that 
	 * this interface can be represented remotely.
	 * 
	 * @return A DesignFactory. Must not be null if a {@link ConfigurationSession} 
	 * is available.
	 */
	SerializableDesignFactory rootDesignFactory();
	
	/**
	 * Get the root element.
	 * 
	 * @return The root element of the configuration. Must not be null if
	 * a ConfiguraitonSession is available.
	 */
	ArooaElement rootElement();
	
}
