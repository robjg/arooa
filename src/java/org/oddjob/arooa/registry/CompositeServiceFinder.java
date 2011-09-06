package org.oddjob.arooa.registry;

/**
 * A {@link ServiceFinder} that combines the results of other
 * {@code ServiceFinder}s. 
 * 
 * @author rob
 *
 */
public class CompositeServiceFinder implements ServiceFinder {

	private ServiceFinder[] finders;
	
	public CompositeServiceFinder(ServiceFinder... finders) {
		this.finders = finders;
	}
	
	@Override
	public Object find(Class<?> cl, String flavour) {
		for (ServiceFinder finder : finders) {
			Object service = finder.find(cl, flavour);
			if (service != null) {
				return service;
			}
		}
		return null;
	}
}
