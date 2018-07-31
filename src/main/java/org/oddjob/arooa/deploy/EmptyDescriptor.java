package org.oddjob.arooa.deploy;

import java.net.URI;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

public class EmptyDescriptor implements ArooaDescriptor {

	private final ClassResolver classResolver;
	
	public EmptyDescriptor(ClassLoader classLoader) {
		classResolver = new ClassLoaderClassResolver(classLoader);
	}
	
	public ClassResolver getClassResolver() {
		return classResolver;
	}
	
	public ArooaBeanDescriptor getBeanDescriptor(
			ArooaClass classIdentifier, PropertyAccessor accessor) {
		return null;
	}
	
	public ConversionProvider getConvertletProvider() {
		return null;
	}
	
	public ElementMappings getElementMappings() {
		return null;
	}
	
	public String getPrefixFor(URI namespace) {
		return null;
	}
}
