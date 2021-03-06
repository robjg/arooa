package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.net.URI;

public class NullArooaDescriptor implements ArooaDescriptor {

	@Override
	public ElementMappings getElementMappings() {
		return null;
	}

	@Override
	public ConversionProvider getConvertletProvider() {
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

	@Override
	public ArooaBeanDescriptor getBeanDescriptor(
			ArooaClass classIdentifier, PropertyAccessor accessor) {
		return null;
	}

	@Override
	public ClassResolver getClassResolver() {
		return null;
	}
}
