package org.oddjob.arooa.registry;

import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.life.ComponentPersistException;
import org.oddjob.arooa.parsing.ArooaContext;

public class MockComponentPool implements ComponentPool {
	
	public ArooaContext contextFor(Object component) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}

	public String getIdFor(Object either) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}
	
	public ComponentTrinity trinityForId(String id) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}
	
	public void configure(Object component) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}

	public void save(Object component) 
	throws ComponentPersistException {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}
	
	public boolean remove(Object component) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}

	public String registerComponent(ComponentTrinity trinity, String id) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}

	@Override
	public Iterable<ComponentTrinity> allTrinities() {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}
	
	@Override
	public ComponentTrinity trinityForContext(ArooaContext context) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}
	
	@Override
	public ComponentTrinity trinityFor(Object either) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}
}
