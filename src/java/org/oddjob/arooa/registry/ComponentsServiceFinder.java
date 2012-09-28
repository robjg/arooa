package org.oddjob.arooa.registry;

import org.oddjob.arooa.ComponentTrinity;

/**
 * A {@link ServiceFinder] for a {@link ComponentPool}.
 * 
 * @author rob
 *
 */
public class ComponentsServiceFinder implements ServiceFinder {

	private final ComponentPool directory;
	
	public ComponentsServiceFinder(ComponentPool directory) {
		this.directory = directory;
	}
	
	public <T> T find(Class<T> cl, String flavour) {
		
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
			if (identifier != null) {
				return cl.cast(lookup.getService(identifier));
			}
		}
		
		return null;
	}
}
