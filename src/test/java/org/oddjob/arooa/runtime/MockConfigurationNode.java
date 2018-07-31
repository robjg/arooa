package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaContext;

public class MockConfigurationNode implements ConfigurationNode {

	public void addNodeListener(ConfigurationNodeListener listener) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public void addText(String text) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public void removeChild(int index) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public void setInsertPosition(int insertAt) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public int insertChild(ConfigurationNode child) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public void removeNodeListener(ConfigurationNodeListener listener) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public ConfigurationHandle parse(ArooaContext parentContext) 
	throws ArooaParseException {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public int indexOf(ConfigurationNode child) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public ArooaContext getContext() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
}
