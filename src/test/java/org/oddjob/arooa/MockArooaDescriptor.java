package org.oddjob.arooa;

import java.net.URI;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;


public class MockArooaDescriptor implements ArooaDescriptor {

	@Override
	public ArooaBeanDescriptor getBeanDescriptor(
			ArooaClass classIdentifier, PropertyAccessor accessor) {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}

	@Override
	public ConversionProvider getConvertletProvider() {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
	@Override
	public ElementMappings getElementMappings() {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
	@Override
	public String getPrefixFor(URI namespace) {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
	@Override
	public ClassResolver getClassResolver() {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
}
