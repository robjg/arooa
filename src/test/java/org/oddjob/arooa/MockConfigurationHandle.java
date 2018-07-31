package org.oddjob.arooa;

import org.oddjob.arooa.parsing.ArooaContext;


public class MockConfigurationHandle implements ConfigurationHandle {

	public ArooaContext getDocumentContext() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public void save() throws ArooaParseException {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	
}
