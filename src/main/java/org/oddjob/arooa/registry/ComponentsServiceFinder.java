package org.oddjob.arooa.registry;

import org.oddjob.arooa.ComponentTrinity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * A {@link ServiceFinder] for a {@link ComponentPool}.
 * <p>
 * @author rob
 *
 */
public class ComponentsServiceFinder implements ServiceFinder {

	private static final Logger logger = 
			LoggerFactory.getLogger(ComponentsServiceFinder.class);
	
	private final ComponentPool directory;
	
	public ComponentsServiceFinder(ComponentPool directory) {
		this.directory = directory;
	}

	@Override
	public <T> T find(Type cl, String flavour) {
		
		for (ComponentTrinity trinity: directory.allTrinities()) {
			
			ServiceProvider provider = null;
			if (trinity.getTheProxy() instanceof ServiceProvider) {
				provider = (ServiceProvider) trinity.getTheProxy();
			}
			else if (trinity.getTheComponent() instanceof ServiceProvider){
				provider = (ServiceProvider) trinity.getTheComponent();
			}
			if (provider == null) {
				continue;
			}
			
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
                logger.debug("Found Service [{}] for {}{} from provider [{}] in the Component Pool.", service, cl.getTypeName(), flavour == null ? "" : ", " + flavour, provider);
			}
			
			return service;
		}
		
		if (logger.isDebugEnabled()) {
            logger.debug("No Service for {}{} found from providers in the Component Pool.", cl.getTypeName(), flavour == null ? "" : ", " + flavour);
		}
		
		return null;
	}
}
