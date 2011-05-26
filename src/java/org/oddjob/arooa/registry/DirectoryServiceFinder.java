package org.oddjob.arooa.registry;

/**
 * A {@link ServiceFinder} that looks through a {@link BeanDirectory}
 * for services.
 * 
 * @author rob
 *
 */
public class DirectoryServiceFinder implements ServiceFinder {

	private final BeanDirectory directory;
	
	/**
	 * Only Constructor.
	 * 
	 * @param directory
	 */
	public DirectoryServiceFinder(BeanDirectory directory) {
		this.directory = directory;
	}
	
	public Object find(Class<?> cl, String flavour) {
		
		Iterable<ServiceProvider> providers = 
			directory.getAllByType(ServiceProvider.class);
		
		for (ServiceProvider provider : providers) {
			
			Services lookup = provider.getServices();
			if (lookup == null) {
				continue;
			}
			
			String identifier = lookup.serviceNameFor(cl, flavour);
			if (identifier != null) {
				return lookup.getService(identifier);
			}
		}
		
		return null;
	}
}
