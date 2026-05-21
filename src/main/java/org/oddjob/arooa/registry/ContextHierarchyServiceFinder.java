package org.oddjob.arooa.registry;

import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.runtime.InstanceRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * A {@link ServiceFinder] that uses the hierarchy of {@link ArooaContext}s
 * to find a service.
 * <p>
 * @author rob
 *
 */
public class ContextHierarchyServiceFinder implements ServiceFinder {

	private static final Logger logger = 
			LoggerFactory.getLogger(ContextHierarchyServiceFinder.class);
	
	private final ArooaContext arooaContext;
	
	/**
	 * Create a new instance of this service finder..
	 * 
	 * @param arooaContext The starting context.
	 */
	public ContextHierarchyServiceFinder(ArooaContext arooaContext) {
		this.arooaContext = arooaContext;
	}
	
	@Override
	public <T> T find(Type cl, String flavour) {
		
		for (ArooaContext context = this.arooaContext; 
				context != null; context = context.getParent()) {
			
			RuntimeConfiguration runtime = context.getRuntime();
			
			if (!(runtime instanceof InstanceRuntimeConfiguration)) {
				continue;
			}
			
			Object instance = ((InstanceRuntimeConfiguration) 
					runtime).getWrappedInstance();
			
			if (instance == null) {
				continue;
			}
			
			if (! (instance instanceof ServiceProvider provider)) {
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
                logger.debug("Found Service [{}] for {}{} from provider [{}] in the Context Hierarchy.", service, cl.getTypeName(), flavour == null ? "" : ", " + flavour, provider);
			}
			
			return service;
		}
		
		if (logger.isDebugEnabled()) {
            logger.debug("No Service for {}{} found from providers in the Context Hierarchy.", cl.getTypeName(), flavour == null ? "" : ", " + flavour);
		}
		
		return null;
	}
}
