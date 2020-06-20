package org.oddjob.arooa.beanutils;

import org.oddjob.arooa.*;
import org.oddjob.arooa.beandocs.MappingsBeanDoc;
import org.oddjob.arooa.beandocs.MappingsContents;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.ArooaDescriptorFactory;
import org.oddjob.arooa.deploy.BeanDescriptorProvider;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.GenericDesignFactory;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.life.ElementsForIdentifier;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.net.URI;
import java.util.*;

/**
 * @oddjob.description Define Magic Beans. Magic Beans are beans who's
 * properties can be defined dynamically. Magic beans are useful when
 * you want to collect information in one bean so it can be kept together.
 * 
 * @oddjob.example Creating a magic bean that define some file information.
 * <p>
 * This is an outer Oddjob configuration file that creates the descriptor
 * that defines properties for a <code>filespec</code> element.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/beanutils/MagicBeansExample.xml}
 * 
 * The nested inner Oddjob configuration uses a list of <code>filespec</code>
 * magic beans to define information for a For Each job.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/beanutils/MagicBeansInner.xml}
 * 
 * The example will display the following when ran:
 * 
 * <pre>
 * Checking Big File (\files\big)
 * less than 1000000 bytes...
 *
 * Checking Medium File (\files\medium)
 * less than 20000 bytes...
 * 
 * Checking Small File (\files\small)
 * less than 3000 bytes...
 * </pre>
 * 
 * @author rob
 *
 */
public class MagicBeanDescriptorFactory implements ArooaDescriptorFactory {

	/**
	 * @oddjob.property
	 * @oddjob.description Definitions for Magic Beans. This will be a
	 * list of {@link MagicBeanDefinition}s.
	 * @oddjob.required. No, but pointless without any definitions.
	 * 
	 */
	private List<MagicBeanDefinition> definitions = 
			new ArrayList<MagicBeanDefinition>();
	
	/**
	 * @oddjob.property
	 * @oddjob.description The namespace for the magic bean element. 
	 * @oddjob.required No.
	 */
	private URI namespace;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The element prefix. 
	 * @oddjob.required No.
	 */
	private String prefix;
	
		
	@Override
	public ArooaDescriptor createDescriptor(ClassLoader classLoader) {

		Mappings mappings = new Mappings();
		
		Map<ArooaClass, BeanDescriptorProvider> descriptorProviders
			 = new HashMap<ArooaClass, BeanDescriptorProvider>();
		
		for (MagicBeanDefinition beanDef : definitions) {
			
			ArooaClass arooaClass = beanDef.createMagic(classLoader);
			
			BeanDescriptorProvider beanDescriptorProvider = 
					beanDef.createMagicBeanDescriptorProvider();
	
			descriptorProviders.put(arooaClass, beanDescriptorProvider);
			
			mappings.put(new ArooaElement(namespace, 
					beanDef.getElement()), arooaClass);
		}
		
		return new Descriptor(mappings, descriptorProviders, classLoader);
	}
	
	
	class Descriptor implements ArooaDescriptor {

		private final Mappings mappings;
		
		private final Map<ArooaClass, BeanDescriptorProvider> 
			descriptorProviders;
		
		private final ClassLoader classLoader;
		
		public Descriptor(Mappings mappings, 
				Map<ArooaClass, BeanDescriptorProvider> descriptorProviders,
				ClassLoader classLoader) {
			this.mappings = mappings;
			this.descriptorProviders = descriptorProviders;
			this.classLoader = classLoader;
		}
		
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass classIdentifier, PropertyAccessor accessor) {

			BeanDescriptorProvider descriptor = 
				descriptorProviders.get(classIdentifier);
			
			if (descriptor == null) {
				return null;
			}
			else {
				return descriptor.getBeanDescriptor(
					classIdentifier, accessor);
			}
		}
		
		@Override
		public ClassResolver getClassResolver() {
			return new ClassLoaderClassResolver(classLoader);
		}
		
		@Override
		public ConversionProvider getConvertletProvider() {
			return null;
		}
		
		@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(null, mappings);
		}
				
		@Override
		public String getPrefixFor(URI namespace) {
			if (namespace == null) {
				return null;
			}
			if (namespace.equals(MagicBeanDescriptorFactory.this.namespace)) {
				return prefix;
			}
			return null;
		}

		@Override
		public String[] getPrefixes() {
			if (prefix == null) {
				return new String[0];
			}
			else {
				return new String[] { prefix };
			}
		}

		@Override
		public URI getUriFor(String prefix) {
			if (prefix == null) {
				return null;
			}
			if (prefix.equals((MagicBeanDescriptorFactory.this.prefix))) {
				return namespace;
			}
			return null;
		}
	}
	
	public void setDefinitions(int index, MagicBeanDefinition def) {
		if (def == null) {
			definitions.remove(index);
		}
		else {
			definitions.add(index, def);
		}
	}
	
	public MagicBeanDefinition getDefinitions(int index) {
		return definitions.get(index);
	}
	
	public URI getNamespace() {
		return namespace;
	}

	@ArooaAttribute
	public void setNamespace(URI namespace) {
		this.namespace = namespace;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	class Mappings implements ElementMappings {

		private Map<ArooaElement, ArooaClass> mappings = 
			new LinkedHashMap<ArooaElement, ArooaClass>();
		
		@Override
		public DesignFactory designFor(ArooaElement element,
				InstantiationContext parentContext) {
			
			ArooaClass arooaClass = mappings.get(element);
			if (arooaClass == null) {
				return null;				
			}
			return new GenericDesignFactory(arooaClass);
		}

		@Override
		public ArooaElement[] elementsFor(
				InstantiationContext propertyContext) {
 			return new ElementsForIdentifier(mappings).elementsFor(
 					propertyContext);
		}
		
		@Override
		public ArooaClass mappingFor(ArooaElement element,
				InstantiationContext parentContext) {
			return mappings.get(element);
		}
		
		void put(ArooaElement element, ArooaClass arooaClass) {
			mappings.put(element, arooaClass);
		}
		
		@Override
		public MappingsContents getBeanDoc(ArooaType arooaType) {
			return new MappingsBeanDoc(mappings);
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": num definitions=" + 
				definitions.size();
	}
}
