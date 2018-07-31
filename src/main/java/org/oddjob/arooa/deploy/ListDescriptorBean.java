package org.oddjob.arooa.deploy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.types.IsType;
import org.oddjob.arooa.utils.ListSetterHelper;

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
	
	/**
	 * Create an empty instance.
	 */
	public ListDescriptorBean() {
	}
	
	/**
	 * Create an instance from an existing collection of 
	 * {@link ArooaDescriptorFactory}s.
	 * 
	 * @param collection
	 */
	public ListDescriptorBean(
			Collection<? extends ArooaDescriptorFactory> collection) {
		descriptors.addAll(collection);
	}
	
	/**
	 * Indexed setter.
	 * 
	 * @param index
	 * @param descriptor
	 */
	public void setDescriptors(int index, ArooaDescriptorFactory descriptor) {
		new ListSetterHelper<ArooaDescriptorFactory>(descriptors).set(
				index, descriptor);
	}
	
	public ArooaDescriptor createDescriptor(ClassLoader loader) {
		
		ListDescriptor listDescriptor = new ListDescriptor();
		
		for (ArooaDescriptorFactory factory : descriptors) {
			
			ArooaDescriptor descriptor = factory.createDescriptor(loader);
			
			if (descriptor != null) {
				listDescriptor.addDescriptor(descriptor);
			}
		}
		
		return listDescriptor;
	}
}
