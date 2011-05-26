package org.oddjob.arooa.standard;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.oddjob.arooa.runtime.PropertyLookup;
import org.oddjob.arooa.runtime.PropertyManager;

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
		this(null, null);
	}
	
	/**
	 * Constructor with some properties. System properties will override
	 * given properties.
	 * 
	 * @param properties
	 */
	public StandardPropertyManager(Properties properties) {
		this(null, properties);
	}
	
	/**
	 * Constructor the defers first to a parent. No system properties 
	 * will be returned unless by the parent.
	 * 
	 * @param parent
	 */
	public StandardPropertyManager(PropertyManager parent) {
		this(parent, null);
	}
	
	/**
	 * Constructor that defers first to parent, then to given properties.
	 * 
	 * @param parent
	 * @param properties
	 */
	public StandardPropertyManager(PropertyManager parent, Properties properties) {
		if (parent == null) {
			lookups.add(new SystemLookup());
		}
		else {
			lookups.add(parent);
		}
		if (properties != null) {
			lookups.add(new StandardPropertyLookup(properties));
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
}

class SystemLookup implements PropertyLookup {
	
	@Override
	public String lookup(String propertyName) {
		return System.getProperty(propertyName);
	}
}
