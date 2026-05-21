package org.oddjob.arooa.reflect;

import java.lang.reflect.Type;

public class MockArooaClass implements ArooaClass {

	@Override
	public Object newInstance() throws ArooaInstantiationException {
		throw new RuntimeException("Unexpected from " + getClass().getName());
	}
	
	@Override
	public Class<?> forClass() {
		throw new RuntimeException("Unexpected from " + getClass().getName());
	}

	@Override
	public Type getType() {
		throw new RuntimeException("Unexpected from " + getClass().getName());
	}

	@Override
	public BeanOverview getBeanOverview(PropertyAccessor accessor) {
		throw new RuntimeException("Unexpected from " + getClass().getName());
	}
}
