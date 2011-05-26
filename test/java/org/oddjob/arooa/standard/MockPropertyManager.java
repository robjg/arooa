package org.oddjob.arooa.standard;

import org.oddjob.arooa.runtime.PropertyLookup;
import org.oddjob.arooa.runtime.PropertyManager;

public class MockPropertyManager implements PropertyManager {

	@Override
	public void addPropertyLookup(PropertyLookup propertyLookup) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public void addPropertyOverride(PropertyLookup propertyLookup) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public void removePropertyLookup(PropertyLookup propertyLookup) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public String lookup(String propertyName) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
}
