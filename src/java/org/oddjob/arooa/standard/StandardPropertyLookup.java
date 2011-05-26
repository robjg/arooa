package org.oddjob.arooa.standard;

import java.util.Properties;

import org.oddjob.arooa.runtime.PropertyLookup;
import org.oddjob.arooa.runtime.PropertyManager;

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

	public StandardPropertyLookup(Properties properties) {
		this.properties.putAll(properties);
	}

	@Override
	public String lookup(String propertyName) {
		return properties.getProperty(propertyName);
	}
}
