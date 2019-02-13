package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * The fundamental unit created during the parsing of a configuration.
 * Generally, a runtime is created for each element of a configuration and
 * form a tree hierarchy.
 * <p/>
 * The runtime handles the lifecycle (initialisation, configuration
 * and destruction) of the wrapped object. Lifecycle instructions cascade
 * to children via {@link RuntimeListener}s, and children add there
 * values back in to the parent using the setter methods:
 * {@link #setProperty(String, Object)},
 * {@link #setIndexedProperty(String, int, Object)},
 * {@link #setMappedProperty(String, String, Object)}.
 * <p/>
 * The runtime is available to client code via a components
 * {@link ArooaContext}.
 *
 * @author rob.
 */
public interface RuntimeConfiguration {

	/**
	 * Add a {@link RuntimeListener}.
	 * 
	 * @param listener The listener. Must not be null.
	 */
	void addRuntimeListener(
			RuntimeListener listener);
	
	/**
	 * Remove a {@link RuntimeListener}.
	 * 
	 * @param listener The listener. Must not be null.
	 */
	void removeRuntimeListener(
			RuntimeListener listener);
	
	/**
	 * Get the name of the class that this RuntimeConfiguration will
	 * be configuring. When this RuntimeConfiguration represents a 
	 * property name, then this class will be the type of the property, not
	 * the parent bean.
	 * 
	 * @return The class.
	 */
	ArooaClass getClassIdentifier();
	
	/**
	 * Initialise this RuntimeConfiguration. This will be called
	 * by the parser after all child nodes have been parsed and
	 * initialised.
	 */
	void init() throws ArooaConfigurationException;

	/**
	 * Configure the object this configuration wraps. 
	 * <p>
	 * This will be called at the component level by client code. 
	 * Child RuntimeConfigurations should listen for and 
	 * propagate configuration events.
	 */
	void configure() throws ArooaConfigurationException;
	
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
	void destroy() throws ArooaConfigurationException;
	
	/**
	 * Set a property on the wrapped object.
	 * 
	 * @param name The name of the property.
	 * @param value The value. May be null.
	 *
	 * @throws ArooaException If setting the property failed.
	 */
    void setProperty(String name, Object value)
    throws ArooaPropertyException;
    
    /**
     * Set a mapped property on the wrapped object.
     * 
     * @param name The name of the property.
     * @param key The key.
     * @param value The value.
     *
     * @throws ArooaException If setting the property failed.
     */
    void setMappedProperty(String name, String key, Object value)
    throws ArooaPropertyException;
    
    /**
     * Set an indexed property on the wrapped object.
     * 
     * @param name The name of the property.
     * @param index The 0 based index.
     * @param value The value.
     *
     * @throws ArooaException If setting the property failed.
     */
    void setIndexedProperty(String name, int index, Object value)
    throws ArooaPropertyException;

}
