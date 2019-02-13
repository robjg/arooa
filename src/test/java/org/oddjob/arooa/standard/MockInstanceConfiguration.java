package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;

public class MockInstanceConfiguration extends InstanceConfiguration {

	final Object proxy;

	final AttributeSetter attributeSetter;

	public MockInstanceConfiguration(
			ArooaClass arooaClass,
			Object wrappedObject,
			ArooaAttributes attrs) {
		this(arooaClass, wrappedObject, wrappedObject, attrs);
	}

	public MockInstanceConfiguration(
			ArooaClass arooaClass,
			Object wrappedObject, 
			Object proxy,
			ArooaAttributes attrs) {
		super(arooaClass, wrappedObject);
        this.proxy = proxy;
        this.attributeSetter = new AttributeSetter(this, attrs);
	}

    @Override
    AttributeSetter getAttributeSetter() {
        return attributeSetter;
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
