package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.net.URI;

public class EmptyDescriptor implements ArooaDescriptor {

	private final ClassResolver classResolver;
	
	public EmptyDescriptor(ClassLoader classLoader) {
		classResolver = new ClassLoaderClassResolver(classLoader);
	}

	@Override
	public ClassResolver getClassResolver() {
		return classResolver;
	}

	@Override
	public ArooaBeanDescriptor getBeanDescriptor(
			ArooaClass classIdentifier, PropertyAccessor accessor) {
		return null;
	}

	@Override
	public ConversionProvider getConvertletProvider() {
		return null;
	}

	@Override
	public ElementMappings getElementMappings() {
		return null;
	}

	@Override
	public String getPrefixFor(URI namespace) {
		return null;
	}

	@Override
	public String[] getPrefixes() {
		return new String[0];
	}

	@Override
	public URI getUriFor(String prefix) {
		return null;
	}
}
