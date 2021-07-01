package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.BeanDescriptorHelper;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.ServiceFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Automatically sets services on bean instances. A single instance of 
 * this class will be used to set properties on a single bean instance.
 * 
 * @author rob
 */
public class AutoSetter {

	private static final Logger logger = LoggerFactory.getLogger(AutoSetter.class);

	private final Set<String> propertiesSetAlready = new HashSet<>();
	
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
			
			ServiceFinder finder = 
					context.getSession().getTools().getServiceHelper(
							).serviceFinderFor(context);

			Class<?> propertyType = overview.getPropertyType(property);
			String qualifier = helper.getFlavour(property);

			Object value;
			try {
				value = finder.find(propertyType, qualifier);
			}
			catch (Exception e) {
				throw new ArooaPropertyException(property, "Unexpected Exception finding service of type " +
						propertyType.getName() + " qualifier " + qualifier +
						" in class " + classIdentifier.forClass().getName(), e);
			}

			if (value == null) {
				logger.debug("No service for property {} of type {} qualifier {} in class {}",
						property, propertyType.getName(), qualifier, classIdentifier.forClass().getName());
			}
			else {
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
}
