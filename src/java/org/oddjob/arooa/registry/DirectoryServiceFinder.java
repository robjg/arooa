package org.oddjob.arooa.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ServiceFinder} that looks through a {@link BeanDirectory}
 * for services.
 * 
 * @author rob
 *
 */
public class DirectoryServiceFinder implements ServiceFinder {

	private static final Logger logger = 
			LoggerFactory.getLogger(DirectoryServiceFinder.class);
		
	private final BeanDirectory directory;
	
	/**
	 * Only Constructor.
	 * 
	 * @param directory
	 */
	public DirectoryServiceFinder(BeanDirectory directory) {
		this.directory = directory;
	}
	
	public <T> T find(Class<T> cl, String flavour) {
		
		Iterable<ServiceProvider> providers = 
			directory.getAllByType(ServiceProvider.class);
		
		for (ServiceProvider provider : providers) {
			
			Services lookup = provider.getServices();
			if (lookup == null) {
				continue;
			}
			
			String identifier = lookup.serviceNameFor(cl, flavour);
			if (identifier == null) {
				continue;
			}
			
			T service = cl.cast(lookup.getService(identifier));
			
			if (logger.isDebugEnabled()) {
				logger.debug("Found Service [" + service + "] for " + 
						cl.getName() + ( flavour == null ? "" : ", " + flavour) +
						" from provider [" + provider + 
						"] in the Bean Directory.");
			}
			
			return service;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("No Service for " + 
					cl.getName() + ( flavour == null ? "" : ", " + flavour) +
					" found from providers in the Bean Directory.");
		}
		
		return null;
	}
}
