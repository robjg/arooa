package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * The fundamental unit created during the parsing of a 
 * configuration.
 * <p>
 * @author rob.
 */
public interface RuntimeConfiguration {

	/**
	 * Add a {@link ConfigurationListner}.
	 * 
	 * @param listener
	 */
	public void addRuntimeListener(
			RuntimeListener listener);
	
	/**
	 * Remove a {@link RuntimeListener}.
	 * 
	 * @param listener
	 */
	public void removeRuntimeListener(
			RuntimeListener listener);
	
	/**
	 * Get the name of the class that this RuntimeConfiguration will
	 * be configuring. When this RuntimeConfiguration represents a 
	 * property name, the this class will be the type of the property, not
	 * the parent bean.
	 * 
	 * @return
	 */
	public ArooaClass getClassIdentifier();
	
	/**
	 * Initialise this RuntimeConfiguration. This will be called
	 * by the parser after all child nodes have been parsed and
	 * initialised.
	 */
	public void init() throws ArooaConfigurationException;

	/**
	 * Configure the object this configuration wraps. 
	 * <p>
	 * This will be called at the component level by client code. 
	 * Child RuntimeConfigurations should listen for and 
	 * propagate configuration events.
	 */
	public void configure() throws ArooaConfigurationException;
	
	/**
	 * Destroy this RuntimeConfiguration.
	 * <p>
	 * This will be called at the component level by client code.
	 * Child RuntimeConfigurations should listen for and 
	 * propagate destroy events.
	 * <p>
	 * Unlike configuration, destroy events should be passed to
	 * child components as well.
	 */
	public void destroy() throws ArooaConfigurationException;
	
	/**
	 * Set a property on the wrapped object.
	 * 
	 * @param name
	 * @param value
	 * @throws ArooaException
	 */
    public void setProperty(String name, Object value) 
    throws ArooaPropertyException;
    
    /**
     * Set a mapped property on the wrapped object.
     * 
     * @param name
     * @param key
     * @param value
     * @throws ArooaException
     */
    public void setMappedProperty(String name, String key, Object value) 
    throws ArooaPropertyException;
    
    /**
     * Set an indexed property on the wrapped object.
     * 
     * @param name
     * @param index
     * @param value
     * @throws ArooaException
     */
    public void setIndexedProperty(String name, int index, Object value) 
    throws ArooaPropertyException;

}
