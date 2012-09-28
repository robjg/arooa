package org.oddjob.arooa.runtime;

/**
 * An {@link RuntimeConfiguration} that wraps an instance of a value
 * or component. 
 * 
 * @author rob
 *
 * @since 1.3
 */
public interface InstanceRuntimeConfiguration extends RuntimeConfiguration {

	/**
	 * Get the instance, if available.
	 * 
	 * @return The instance or null if it is not yet available.
	 */
	public Object getWrappedInstance();
}
