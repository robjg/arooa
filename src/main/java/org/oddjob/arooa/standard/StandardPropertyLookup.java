package org.oddjob.arooa.standard;

import java.util.Properties;
import java.util.Set;

import org.oddjob.arooa.runtime.PropertyLookup;
import org.oddjob.arooa.runtime.PropertyManager;
import org.oddjob.arooa.runtime.PropertySource;

/**
 * A simple implementation of a {@link PropertyLookup}.
 * 
 * @see PropertyManager
 * 
 * @author rob
 *
 */
public class StandardPropertyLookup implements PropertyLookup {

	private final Properties properties = new Properties();

	private final PropertySource source; 
	
	public StandardPropertyLookup(Properties properties, final String source) {
		if (source == null) {
			throw new NullPointerException("Property source is null.");
		}
		if (properties == null) {
			throw new NullPointerException("No properties.");
		}
		this.properties.putAll(properties);
		this.source = new PropertySource() {
			@Override
			public String toString() {
				return source;
			}
		};
	}

	@Override
	public String lookup(String propertyName) {
		return properties.getProperty(propertyName);
	}

	@Override
	public PropertySource sourceFor(String propertyName) {
		if (properties.containsKey(propertyName)) {
			return source;
		}
		else {
			return null;
		}
	}
	
	@Override
	public Set<String> propertyNames() {
		return properties.stringPropertyNames();
	}
}
