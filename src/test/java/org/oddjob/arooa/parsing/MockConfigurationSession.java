package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;

public class MockConfigurationSession implements ConfigurationSession {

	public DragPoint dragPointFor(Object component) {
		throw new RuntimeException("Unexpected from class " + getClass());
	}

	public ArooaDescriptor getArooaDescriptor() {
		throw new RuntimeException("Unexpected from class " + getClass());
	}

	public boolean isModified() {
		throw new RuntimeException("Unexpected from class " + getClass());
	}
	
	public void addSessionStateListener(SessionStateListener listener) {
		throw new RuntimeException("Unexpected from class " + getClass());
	}
	
	public void removeSessionStateListener(SessionStateListener listener) {
		throw new RuntimeException("Unexpected from class " + getClass());
	}
	
	public void save() throws ArooaParseException {
		throw new RuntimeException("Unexpected from class " + getClass());
	}
}
