package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaConfigurationException;

public class MockArooaHandler implements ArooaHandler {

	@Override
	public ArooaContext onStartElement(ArooaElement element,
			ArooaContext parentContext) throws ArooaConfigurationException {
		throw new RuntimeException("Unexpected from class " + getClass());
	}
}
