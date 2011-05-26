package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;

public class MockInstanceConfiguration extends InstanceConfiguration {

	Object proxy;
	
	public MockInstanceConfiguration() {
		super(null, null, new MutableAttributes());
	}
	
	public MockInstanceConfiguration(
			ArooaClass arooaClass,
			Object wrappedObject,
			ArooaAttributes attrs) {
		super(arooaClass, wrappedObject, attrs);
	}

	public MockInstanceConfiguration(
			ArooaClass arooaClass,
			Object wrappedObject, 
			Object proxy,
			ArooaAttributes attrs) {
		super(arooaClass, wrappedObject, attrs);
		this.proxy = proxy;
	}
	
	@Override
	Object getObjectToSet() {
		if (proxy == null) {
			throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
		}
		else {
			return proxy;
		}
	}
		
	@Override
	InjectionStrategy injectionStrategy() {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
	@Override
	void configure(InstanceRuntime ourWrapper, 
			ArooaContext context) throws ArooaException {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}

	@Override
	void init(InstanceRuntime ourWrapper, 
			ArooaContext context) throws ArooaException {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}

	@Override
	void destroy(InstanceRuntime ourWrapper, ArooaContext context)
			throws ArooaException {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
	@Override
	void listenerConfigure(InstanceRuntime ourWrapper, 
			ArooaContext context) throws ArooaException {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}

	@Override
	void listenerDestroy(InstanceRuntime ourWrapper, ArooaContext context)
			throws ArooaException {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
}
