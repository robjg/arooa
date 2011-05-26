package org.oddjob.arooa.deploy;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.types.IsType;

/**
 * @oddjob.description An Arooa Descriptor Factory that is a container
 * for a collection of other descriptors. The other descriptors will
 * most probably be {@link ArooaDescriptorBean}s.
 * <p>
 * This type can be used wherever an {@link ArooaDescriptorBean} can
 * be used.
 * 
 * @oddjob.example
 * 
 * Oddjob's descriptor!. Note that it started life before the descriptor 
 * elements were created, and so {@link IsType} is used instead of 
 * {@link BeanDefinition} elements.
 * 
 * {@oddjob.xml.resource META-INF/arooa.xml}
 * 
 * @author rob
 *
 */
public class ListDescriptorBean implements ArooaDescriptorFactory {

	/**
	 * @oddjob.property
	 * @oddjob.description A list of Arooa Descriptor Factories.
	 * @oddjob.required. No, but pointless if missing.
	 */
	private final List<ArooaDescriptorFactory> descriptors =
		new ArrayList<ArooaDescriptorFactory>();
	
	public void setDescriptors(int ignored, ArooaDescriptorFactory descriptor) {
		descriptors.add(descriptor);
	}
	
	public ArooaDescriptor createDescriptor(ClassLoader loader) {
		
		ListDescriptor descriptor = new ListDescriptor();
		
		for (ArooaDescriptorFactory factory : descriptors) {
			descriptor.addDescriptor(factory.createDescriptor(loader));
		}
		
		return descriptor;
	}
}
