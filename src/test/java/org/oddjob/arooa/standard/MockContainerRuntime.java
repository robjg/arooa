package org.oddjob.arooa.standard;

import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaHandler;

public class MockContainerRuntime extends ContainerRuntime {

	public MockContainerRuntime(
			PropertyDefinition propertyDefinition,
			ArooaContext context) {
		super(propertyDefinition, context);
	}

	@Override
	ArooaHandler getHandler() {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}

}
