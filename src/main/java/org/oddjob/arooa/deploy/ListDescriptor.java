package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * A collection of descriptors.
 * 
 * @see ListDescriptorBean
 * 
 * @author rob
 *
 */
public class ListDescriptor implements ArooaDescriptor {

	private final List<ArooaDescriptor> descriptors =
			new LinkedList<>();

	private final ClassMappingsList mappings = 
		new ClassMappingsList();

	private final PrefixMappings prefixMappings = new SimplePrefixMappings();

	public ListDescriptor() {
	}
	
	public ListDescriptor(ArooaDescriptor... descriptors) {
		for (ArooaDescriptor descriptor: descriptors) {
			addDescriptor(descriptor);
		}
	}

	public ListDescriptor(Collection<ArooaDescriptor> descriptors) {
		for (ArooaDescriptor descriptor: descriptors) {
			addDescriptor(descriptor);
		}
	}

	public void addDescriptor(ArooaDescriptor descriptor) {
		descriptors.add(0, descriptor);
		
		ElementMappings mappings = descriptor.getElementMappings();
		if (mappings != null) {
			this.mappings.addMappings(mappings);
		}

		prefixMappings.add(descriptor);
	}

	public int size() {
		return descriptors.size();
	}
	
	public ConversionProvider getConvertletProvider() {
		
		return registry -> {

			List<ArooaDescriptor> reversed =
					new ArrayList<>(descriptors);
			Collections.reverse(reversed);

			for (ArooaDescriptor descriptor: reversed) {
				ConversionProvider convertletProvider =
					descriptor.getConvertletProvider();

				if (convertletProvider != null) {
					convertletProvider.registerWith(
							registry);
				}
			}
		};
	}
	
	public ArooaBeanDescriptor getBeanDescriptor(
			ArooaClass classIdentifier, PropertyAccessor accessor) {

		for (ArooaDescriptor descriptor : descriptors) {
			ArooaBeanDescriptor beanDescriptor = descriptor.getBeanDescriptor(
				classIdentifier, accessor);
			if (beanDescriptor != null) {
				return beanDescriptor;
			}
		}
		
		return null;
	}
	
	@Override
	public ElementMappings getElementMappings() {
		return mappings;
	}
	
	@Override
	public String getPrefixFor(URI namespace) {
		return prefixMappings.getPrefixFor(namespace);
	}

	@Override
	public URI getUriFor(String prefix) {
		return prefixMappings.getUriFor(prefix);
	}

	@Override
	public String[] getPrefixes() {
		return prefixMappings.getPrefixes();
	}

	@Override
	public ClassResolver getClassResolver() {
		return new ClassResolver() {
			public Class<?> findClass(String className) {
				for (ArooaDescriptor descriptor : descriptors) {
					ClassResolver resolver = descriptor.getClassResolver();
					if (resolver == null) {
						continue;
					}
					Class<?> cl = resolver.findClass(className);
					if (cl != null) {
						return cl;
					}
				}
				return null;
			}
			public URL getResource(String resource) {
				for (ArooaDescriptor descriptor : descriptors) {
					ClassResolver resolver = descriptor.getClassResolver();
					if (resolver == null) {
						continue;
					}
					URL url = resolver.getResource(resource);
					if (url != null) {
						return url;
					}
				}
				return null;
			}
			public URL[] getResources(String resource) {
				Collection<URL> results = new LinkedHashSet<>();
				for (ArooaDescriptor descriptor : descriptors) {
					ClassResolver resolver = descriptor.getClassResolver();
					if (resolver == null) {
						continue;
					}
					URL[] urls = resolver.getResources(resource);
					if (urls == null) {
						throw new NullPointerException(
								"Null resource from Descriptor ClassResolver " +
								descriptor + ".");
					}
					results.addAll(Arrays.asList(urls));
				}
				return results.toArray(new URL[0]);
			}
			public ClassLoader[] getClassLoaders() {
				Collection<ClassLoader> results =
						new LinkedHashSet<>();
				
				for (ArooaDescriptor descriptor : descriptors) {
					ClassResolver resolver = descriptor.getClassResolver();
					if (resolver == null) {
						continue;
					}
					ClassLoader[] classLoaders = resolver.getClassLoaders();
					if (classLoaders  == null) {
						throw new NullPointerException(
								"Null ClassLoader from Descriptor ClassResolver " +
								descriptor + ".");
					}
					results.addAll(Arrays.asList(classLoaders));
				}
				return results.toArray(new ClassLoader[0]);
			}
		};
	}
}
