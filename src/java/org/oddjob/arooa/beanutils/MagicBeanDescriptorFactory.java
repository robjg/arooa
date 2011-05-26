package org.oddjob.arooa.beanutils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.beandocs.MappingsContents;
import org.oddjob.arooa.beandocs.MappingsBeanDoc;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.ArooaDescriptorFactory;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.GenericDesignFactory;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.life.ElementsForIdentifier;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * @oddjob.description Define Magic Beans. Magic Beans are beans who's
 * properties can be defined dynamically. 
 * 
 * @author rob
 *
 */
public class MagicBeanDescriptorFactory implements ArooaDescriptorFactory {

	public String NAMESPACE = "http://rgordon.co.uk/oddjob/magic";
	
	/**
	 * @oddjob.property
	 * @oddjob.description Definitions for Magic Beans. This will be a
	 * list of {@link MagicBeanDefinition}s.
	 * @oddjob.required. No, but pointless without any definitions.
	 * 
	 */
	private List<MagicBeanDefinition> definitions = new ArrayList<MagicBeanDefinition>();
	
	/**
	 * @oddjob.property
	 * @oddjob.description The namespace for the magic bean element. 
	 * @oddjob.required No. Defaults to http://rgordon.co.uk/oddjob/magic
	 */
	private URI namespace;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The element prefix. 
	 * @oddjob.required No. Defaults to 'magic'
	 */
	private String prefix = "magic";
	
	public MagicBeanDescriptorFactory() {
		try {
			namespace = new URI(NAMESPACE);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
		
	@Override
	public ArooaDescriptor createDescriptor(ClassLoader classLoader) {

		Mappings mappings = new Mappings();
		
		for (MagicBeanDefinition beanDef : definitions) {
			mappings.put(new ArooaElement(namespace, beanDef.getName()),
					beanDef.createMagic(classLoader));
		}
		
		return new Descriptor(mappings, classLoader);		
	}
	
	class Descriptor implements ArooaDescriptor {

		private final Mappings mappings;
		
		private final ClassLoader classLoader;
		
		public Descriptor(Mappings mappings, 
				ClassLoader classLoader) {
			this.mappings = mappings;
			this.classLoader = classLoader;
		}
		
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass classIdentifier, PropertyAccessor accessor) {
			return null;
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
			if (MagicBeanDescriptorFactory.this.namespace == namespace) {
				return prefix;
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
}
