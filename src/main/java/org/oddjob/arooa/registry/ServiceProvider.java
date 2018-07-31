package org.oddjob.arooa.registry;

/**
 * A provider of {@link Services}. If a component implements
 * this interface it is able to provide services via
 * auto-wiring. 
 * 
 * @author rob
 *
 */
public interface ServiceProvider {
	
	/**
	 * Provider the services.
	 * 
	 * @return The services. May be null.
	 */
	public Services getServices();

}
