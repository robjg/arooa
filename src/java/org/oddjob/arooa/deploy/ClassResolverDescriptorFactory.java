package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ClassResolver;

/**
 * Create an {@link ArooaDescriptor} by scanning an existing 
 * descriptor for descriptor files.
 * <p>
 * This ArooaDescriptorFactory Returns null if there are no
 * descriptors found.
 * 
 * @author rob
 *
 */
public class ClassResolverDescriptorFactory 
implements ArooaDescriptorFactory {

	private final String resource;
	
	private final ClassResolver resolver;
	
	public ClassResolverDescriptorFactory(
			String resource, ClassResolver existing) {
		this.resource = resource;
		this.resolver = existing;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.deploy.ArooaDescriptorFactory#createDescriptor(java.lang.ClassLoader)
	 */
	@Override
	public ArooaDescriptor createDescriptor(ClassLoader ignored) {
		
		ClassLoader[] classLoaders = resolver.getClassLoaders();
				
		if (classLoaders.length == 0) {
			return null;
		}
		
		ListDescriptor listDescriptor = 
			new ListDescriptor();
						
		for (ClassLoader loader : classLoaders) {
			
			ClassPathDescriptorFactory descriptorFactory = 
				new ClassPathDescriptorFactory();
			descriptorFactory.setResource(resource);
			descriptorFactory.setExcludeParent(true);
			
			ArooaDescriptor descriptor = descriptorFactory.createDescriptor(
					loader);
			
			if (descriptor == null) {
				continue;
			}
			
			listDescriptor.addDescriptor(descriptor);
 		}
		
		return listDescriptor;
	}
		
	public String getResource() {
		return resource;
	}
	
	public ClassResolver getResolver() {
		return resolver;
	}
}
