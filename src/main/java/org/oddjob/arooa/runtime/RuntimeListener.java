package org.oddjob.arooa.runtime;

import java.util.EventListener;

import org.oddjob.arooa.ArooaConfigurationException;

/**
 * Able to listen to configuration events from {@link RuntimeConfiguration}s.
 * <p>
 * Implementations will mainly be child RuntimeConfigurations that wish
 * to know when there parents are being configured.
 * 
 * @author rob
 *
 */
public interface RuntimeListener extends EventListener {

    /**
     * Called by an {@link RuntimeConfiguration} before performing the
     * initialisation phase.
     *
     * @param event The event.
     *
     * @throws ArooaConfigurationException If the listener detects a configuration
     * problem.
     */
	void beforeInit(RuntimeEvent event)
	throws ArooaConfigurationException;

    /**
     * Called by an {@link RuntimeConfiguration} after performing the
     * initialisation phase.
     *
     * @param event The event. Never null.
     *
     * @throws ArooaConfigurationException If the listener detects a configuration
     * problem.
     */
	void afterInit(RuntimeEvent event)
	throws ArooaConfigurationException;

    /**
     * Called by an {@link RuntimeConfiguration} before performing the
     * configuration phase.
     *
     * @param event The event. Never null.
     *
     * @throws ArooaConfigurationException If the listener detects a configuration
     * problem.
     */
	void beforeConfigure(RuntimeEvent event)
	throws ArooaConfigurationException;

    /**
     * Called by an {@link RuntimeConfiguration} after performing the
     * configuration phase.
     *
     * @param event The event. Never Null.
     *
     * @throws ArooaConfigurationException If the listener detects a configuration
     * problem.
     */
	void afterConfigure(RuntimeEvent event)
	throws ArooaConfigurationException;

    /**
     * Called by an {@link RuntimeConfiguration} before performing the
     * destroy phase.
     *
     * @param event The event. Never null.
     */
	void beforeDestroy(RuntimeEvent event);

    /**
     * Called by an {@link RuntimeConfiguration} after performing the
     * destroy phase.
     *
     * @param event The event. Never null.
     */
	void afterDestroy(RuntimeEvent event);
}
