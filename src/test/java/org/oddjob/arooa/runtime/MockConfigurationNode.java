package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ParseContext;

public class MockConfigurationNode implements ConfigurationNode<ArooaContext> {

	@Override
	public void addNodeListener(ConfigurationNodeListener<ArooaContext> listener) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public void addText(String text) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public void removeChild(int index) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public void setInsertPosition(int insertAt) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public int insertChild(ConfigurationNode<ArooaContext> child) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public void removeNodeListener(ConfigurationNodeListener<ArooaContext> listener) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
	throws ArooaParseException {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public int indexOf(ConfigurationNode<?> child) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public ArooaContext getContext() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
}
