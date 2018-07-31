
package org.oddjob.arooa.standard;

import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import org.oddjob.arooa.runtime.PropertyLookup;
import org.oddjob.arooa.runtime.PropertyManager;
import org.oddjob.arooa.runtime.PropertySource;

/**
 * Standard implementation of a {@link PropertyManager}.
 * 
 * @author rob
 *
 */
public class StandardPropertyManager implements PropertyManager {

	private final List<PropertyLookup> lookups = 
		new CopyOnWriteArrayList<PropertyLookup>();
	
	/**
	 * Default Constructor. Initially only system properties are available.
	 */
	public StandardPropertyManager() {
		this(null, null, null);
	}
	
	/**
	 * Constructor with some properties. System properties will override
	 * given properties.
	 * 
	 * @param properties
	 * @param source The name of the source of the properties.
	 */
	public StandardPropertyManager(Properties properties, String source) {
		this(null, properties, source);
	}
	
	/**
	 * Constructor the defers first to a parent. No system properties 
	 * will be returned unless by the parent.
	 * 
	 * @param parent
	 */
	public StandardPropertyManager(PropertyManager parent) {
		this(parent, null, null);
	}
	
	/**
	 * Constructor that defers first to parent, then to given properties.
	 * 
	 * @param parent
	 * @param properties
	 * @param source The name of the source of the properties.
	 */
	public StandardPropertyManager(PropertyManager parent, 
			Properties properties, String source) {
		if (parent == null) {
			lookups.add(new SystemLookup());
		}
		else {
			lookups.add(parent);
		}
		if (properties != null) {
			lookups.add(new StandardPropertyLookup(properties, source));
		}
	}

	@Override
	public void addPropertyLookup(PropertyLookup propertyLookup) {
		if (propertyLookup == null) {
			throw new NullPointerException("No Lookup.");
		}
		lookups.add(propertyLookup);
	}
	
	@Override
	public void addPropertyOverride(PropertyLookup propertyLookup) {
		if (propertyLookup == null) {
			throw new NullPointerException("No Lookup.");
		}
		lookups.add(0, propertyLookup);
	}
	
	@Override
	public void removePropertyLookup(PropertyLookup propertyLookup) {
		lookups.remove(propertyLookup);
	}
	
	@Override
	public String lookup(String propertyName) {
		for (PropertyLookup lookup : lookups) {
			String value = lookup.lookup(propertyName);
			if (value != null) {
				return value;
			}			
		}
		return null;
	}
	
	@Override
	public Set<String> propertyNames() {
		Set<String> propertyNames = new TreeSet<String>();
		for (PropertyLookup lookup : lookups) {
			propertyNames.addAll(lookup.propertyNames());
		}
		return propertyNames;
	}
	
	@Override
	public PropertySource sourceFor(String propertyName) {
		for (PropertyLookup lookup : lookups) {
			PropertySource source = lookup.sourceFor(propertyName);
			if (source != null) {
				return source;
			}			
		}
		return null;
	}
}

class SystemLookup implements PropertyLookup {
	
	@Override
	public String lookup(String propertyName) {
		return System.getProperty(propertyName);
	}

	@Override
	public Set<String> propertyNames() {
		return System.getProperties().stringPropertyNames();
	}
	
	@Override
	public PropertySource sourceFor(String propertyName) {
		if (System.getProperties().containsKey(propertyName)) {
			return SYSTEM_PROPERTY_SOURCE;
		}
		else {
			return null;
		}
	}	
}
