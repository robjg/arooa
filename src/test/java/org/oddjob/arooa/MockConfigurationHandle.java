package org.oddjob.arooa;

import org.oddjob.arooa.parsing.ParseContext;


public class MockConfigurationHandle<P extends ParseContext<P>> implements ConfigurationHandle<P> {

	public P getDocumentContext() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public void save() throws ArooaParseException {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	
}
