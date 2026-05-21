package org.oddjob.arooa.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

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
	 * @param directory A Bean Directory.
	 */
	public DirectoryServiceFinder(BeanDirectory directory) {
		this.directory = directory;
	}

	@Override
	public <T> T find(Type cl, String flavour) {
		
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
			
			@SuppressWarnings("unchecked")
			T service = (T) (lookup.getService(identifier));
			
			if (logger.isDebugEnabled()) {
                logger.debug("Found Service [{}] for {}{} from provider [{}] in the Bean Directory.", service, cl.getTypeName(), flavour == null ? "" : ", " + flavour, provider);
			}
			
			return service;
		}
		
		if (logger.isDebugEnabled()) {
            logger.debug("No Service for {}{} found from providers in the Bean Directory.", cl.getTypeName(), flavour == null ? "" : ", " + flavour);
		}
		
		return null;
	}
}
