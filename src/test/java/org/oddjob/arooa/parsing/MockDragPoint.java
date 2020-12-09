package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.registry.ChangeHow;

public class MockDragPoint implements DragPoint {

	@Override
	public DragTransaction beginChange(ChangeHow how) {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public String cut() {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public String copy() {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public void delete() {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public void paste(int index, String config) {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public boolean supportsCut() {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public boolean supportsPaste() {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
			throws ArooaParseException {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	
}
