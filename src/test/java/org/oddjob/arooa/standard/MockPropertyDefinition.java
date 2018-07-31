package org.oddjob.arooa.standard;

import org.oddjob.arooa.reflect.ArooaClass;

public class MockPropertyDefinition implements PropertyDefinition {

	public String getPropertyName() {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
	public ArooaClass getPropertyType() {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
}
