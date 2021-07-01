package org.oddjob.arooa.registry;

/**
 * A Lookup for services. Looking up services is a two step
 * process. First get the name of the service, then use the
 * name to get the service. This is to allow services to be
 * accessed directly in configuration using the mapped property
 * syntax my-component.services(executor-service).
 * <p>
 * The Service name can be anything but so that it can be used
 * in configuration it is advisable that it doesn't contain
 * any of reserved characters <code>.[]()</code>
 * <p>
 * An optional flavour can be specified which may affect 
 * the service provided. For instance
 * <code>serviceNameFor(ExecutorService.class, "pooled")</code>
 * might return "executor-service;pooled", which can the be passed
 * to the @{link {@link #getService(String)} method.
 * <p>
 * Implementations should document what services they provide
 * and there names.
 * 
 * @author rob
 *
 */
public interface Services {

	/**
	 * If these services can provide a service of the given
	 * class then a name that identifies the service is provided.
	 *  
	 * @param theClass The type of service.
	 * @param flavour An optional flavour. May be null.
	 * 
	 * @return A service name, or null if none can be provided.
	 */
	String serviceNameFor(Class<?> theClass, String flavour);
	
	/**
	 * Provide the service for the given name.
	 * 
	 * @param serviceName
	 * 
	 * @return A service for the name.
	 * 
	 * @throws IllegalArgumentException If no service for the
	 * name can be provided.
	 */
	Object getService(String serviceName)
	throws IllegalArgumentException;
	
}
