package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.registry.ChangeHow;

public class MockDragPoint implements DragPoint {

	public DragTransaction beginChange(ChangeHow how) {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	public String copy() {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	public void cut() {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	public void paste(int index, String config) throws ArooaParseException {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	public boolean supportsCut() {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	public boolean supportsPaste() {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	public ConfigurationHandle parse(ArooaContext parentContext)
			throws ArooaParseException {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	
}
