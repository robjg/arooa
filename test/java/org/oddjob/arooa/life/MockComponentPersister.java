package org.oddjob.arooa.life;

import org.oddjob.arooa.ArooaSession;

public class MockComponentPersister implements ComponentPersister {

	@Override
	public void persist(String id, Object proxy, ArooaSession session) {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public void remove(String id, ArooaSession session) {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public Object restore(String id, ClassLoader classLoader,
			ArooaSession session) {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public String[] list() throws ComponentPersistException {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public void clear() {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public void close() {
		throw new RuntimeException("Unexpected from " + getClass());
	}
}
