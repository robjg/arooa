package org.oddjob.arooa.registry;

import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.runtime.InstanceRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

/**
 * A {@link ServiceFinder] that uses the hierarchy of {@link ArooaContext}s
 * to find a service.
 * 
 * @author rob
 *
 */
public class ContextHierarchyServiceFinder implements ServiceFinder {

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
	public <T> T find(Class<T> cl, String flavour) {
		
		
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
			
			if (! (instance instanceof ServiceProvider)) {
				continue;
			}
						
			ServiceProvider provider = (ServiceProvider) instance;
						
			Services lookup = provider.getServices();
			if (lookup == null) {
				continue;
			}
			
			String identifier = lookup.serviceNameFor(cl, flavour);
			if (identifier != null) {
				return cl.cast(lookup.getService(identifier));
			}
		}
		
		return null;
	}
}
