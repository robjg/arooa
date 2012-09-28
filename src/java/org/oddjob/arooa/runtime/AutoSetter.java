package org.oddjob.arooa.runtime;

import java.util.HashSet;
import java.util.Set;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.BeanDescriptorHelper;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.ComponentsServiceFinder;
import org.oddjob.arooa.registry.CompositeServiceFinder;
import org.oddjob.arooa.registry.ContextHierarchyServiceFinder;
import org.oddjob.arooa.registry.DirectoryServiceFinder;
import org.oddjob.arooa.registry.ServiceFinder;

/**
 * Automatically sets services on bean instances. A single instance of 
 * this class will be used to set properties on a single bean instance.
 * 
 * @author rob
 */
public class AutoSetter {

	private final Set<String> propertiesSetAlready = new HashSet<String>();
	
	private boolean itsUsDoingTheSetting;
	
	/**
	 * Allows a property to be marked as already set. This allows configuration
	 * to override the auto setting.
	 * 
	 * @param propertyName
	 */
	public void markAsSet(String propertyName) {
		if (!itsUsDoingTheSetting) {
			propertiesSetAlready.add(propertyName);
		}
	}
	
	public void setServices(ArooaContext context) 
	throws ArooaPropertyException {
		ArooaSession session = context.getSession();
		
		RuntimeConfiguration runtime = context.getRuntime();
		
		ArooaClass classIdentifier = runtime.getClassIdentifier();
		
		PropertyAccessor accessor = session.getTools().getPropertyAccessor();
		
		BeanDescriptorHelper helper =
			new BeanDescriptorHelper(
					session.getArooaDescriptor().getBeanDescriptor(
							classIdentifier, accessor));
		
		BeanOverview overview = runtime.getClassIdentifier(
				).getBeanOverview(accessor);
		
		String[] properties = overview.getProperties();
		
		for (String property: properties) {
			if (propertiesSetAlready.contains(property)) {
				continue;
			}
			if (!helper.isAuto(property)) {
				continue;
			}
			if (!overview.hasWriteableProperty(property)) {
				throw new ArooaException("Auto property isn't writable: " + 
						property + " of " + classIdentifier);
			}
			if (helper.isComponent(property)) {
				throw new ArooaException("Property can't be Auto and a Component: " + 
						property + " of " + classIdentifier);
			}
			if (overview.isIndexed(property)) {
				throw new ArooaException("Property can't be Auto and Indexed: " + 
						property + " of " + classIdentifier);
			}
			if (overview.isMapped(property)) {
				throw new ArooaException("Property can't be Auto and Mapped: " + 
						property + " of " + classIdentifier);
			}
			
			ServiceFinder finder = new CompositeServiceFinder(
					new ContextHierarchyServiceFinder(context),
					new DirectoryServiceFinder(session.getBeanRegistry()),
					new ComponentsServiceFinder(session.getComponentPool())
				);
			
			Object value = finder.find(
					overview.getPropertyType(property), 
					helper.getFlavour(property));
			
			
			try {
				itsUsDoingTheSetting = true;
				runtime.setProperty(property, value);
			}
			finally {
				itsUsDoingTheSetting = false;
			}
		}
	}
}
