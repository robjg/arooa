package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.reflect.ArooaClass;

public class MockRuntimeConfiguration implements RuntimeConfiguration {

	public void addRuntimeListener(RuntimeListener listener) {
		throw new RuntimeException("Unexpected from class " + 
				getClass().getName());
	}
	
	public void removeRuntimeListener(RuntimeListener listener) {
		throw new RuntimeException("Unexpected from class " + 
				getClass().getName());
	}

	public ArooaClass getClassIdentifier() {
		throw new RuntimeException("Unexpected from class " + 
				getClass().getName());
	}
	
	public void init() throws ArooaException {
		throw new RuntimeException("Unexpected from class " + 
				getClass().getName());
	}

	public void configure() {
		throw new RuntimeException("Unexpected from class " + 
				getClass().getName());
	}

	public void setIndexedProperty(String name, int index, Object value) {
		throw new RuntimeException("Unexpected from class " + 
				getClass().getName());
	}

	public void setMappedProperty(String name, String key, Object value) {
		throw new RuntimeException("Unexpected from class " + 
				getClass().getName());
	}

	public void setProperty(String name, Object value) throws ArooaException {
		throw new RuntimeException("Unexpected from class " + 
				getClass().getName());
	}

	public void destroy() {
		throw new RuntimeException("Unexpected from class " + 
				getClass().getName());
	}
}
