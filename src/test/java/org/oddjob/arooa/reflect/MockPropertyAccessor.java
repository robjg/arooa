package org.oddjob.arooa.reflect;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.convert.ArooaConverter;

public class MockPropertyAccessor implements PropertyAccessor {

	public ArooaClass getClassName(Object bean) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public BeanOverview getBeanOverview(Class<?> forClass) throws ArooaException {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public Object getProperty(Object bean, String property) throws ArooaException {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public <T> T getProperty(Object bean, 
			String property, Class<T> required) 
	throws ArooaException {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public void setMappedProperty(Object bean, String name, String key, Object value) throws ArooaException {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public void setIndexedProperty(Object bean, String name, int index, Object value) throws ArooaException {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public void setSimpleProperty(Object bean, String name, Object value) throws ArooaException {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public void setProperty(Object bean, String name, Object value) throws ArooaException {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public PropertyAccessor accessorWithConversions(ArooaConverter converter) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
}
