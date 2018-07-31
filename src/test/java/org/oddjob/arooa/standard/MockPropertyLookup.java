package org.oddjob.arooa.standard;

import java.util.Set;

import org.oddjob.arooa.runtime.PropertyLookup;
import org.oddjob.arooa.runtime.PropertySource;

public class MockPropertyLookup implements PropertyLookup {

	@Override
	public String lookup(String propertyName) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public Set<String> propertyNames() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public PropertySource sourceFor(String propertyName) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
}
