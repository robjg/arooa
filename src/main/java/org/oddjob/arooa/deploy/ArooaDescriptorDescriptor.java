package org.oddjob.arooa.deploy;

import org.oddjob.arooa.*;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.net.URI;

/**
 * An {@link ArooaDescriptor} for a {@link ArooaConfiguration}
 * that provides and {@link ArooaDescriptorBean}.
 *  
 * @author rob
 *
 */
public class ArooaDescriptorDescriptor implements ArooaDescriptor {

	private final ArooaDescriptor descriptor;

	public ArooaDescriptorDescriptor() {
		
		ArooaDescriptorDescriptorFactory factory = 
			new ArooaDescriptorDescriptorFactory();
			
		descriptor = factory.createDescriptor(
				ArooaDescriptorDescriptorFactory.class.getClassLoader());
	}

	@Override
	public ElementMappings getElementMappings() {
		return descriptor.getElementMappings();
	}
	
	@Override
	public ConversionProvider getConvertletProvider() {
		return descriptor.getConvertletProvider();
	}
	
	@Override
	public ArooaBeanDescriptor getBeanDescriptor(
			ArooaClass classIdentifier,
			PropertyAccessor accessor) {
		return descriptor.getBeanDescriptor(
				classIdentifier, accessor);
	}
	
	@Override
	public String getPrefixFor(URI namespace) {
		return descriptor.getPrefixFor(namespace);
	}

	@Override
	public String[] getPrefixes() {
		return descriptor.getPrefixes();
	}

	@Override
	public URI getUriFor(String prefix) {
		return descriptor.getUriFor(prefix);
	}

	@Override
	public ClassResolver getClassResolver() {
		return descriptor.getClassResolver();
	}
	
}
