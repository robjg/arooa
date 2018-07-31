package org.oddjob.arooa.standard;

import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.runtime.RuntimeListener;

public class MockInstanceRuntime extends InstanceRuntime {
	
	MockInstanceRuntime() {
		super(null, null);
	}
	
	MockInstanceRuntime(InstanceConfiguration instance,
			ArooaContext context) {
		super(instance, context);
	}

	@Override
	public void addRuntimeListener(RuntimeListener listener) {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
	@Override
	public void removeRuntimeListener(RuntimeListener listener) {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
	
	@Override
	ParentPropertySetter getParentPropertySetter() {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
		
	@Override
	ArooaHandler getHandler() {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
}
