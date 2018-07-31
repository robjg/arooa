package org.oddjob.arooa.deploy;

import java.net.URI;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

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
	public ClassResolver getClassResolver() {
		return descriptor.getClassResolver();
	}
	
}
