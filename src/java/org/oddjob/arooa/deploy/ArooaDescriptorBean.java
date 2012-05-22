/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.deploy;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.beandocs.MappingsContents;
import org.oddjob.arooa.beandocs.MappingsBeanDoc;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.life.ElementsForIdentifier;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.utils.ClassesUtils;
import org.oddjob.arooa.utils.ListSetterHelper;

/**
 * A bean style implementation of an {@link ArooaDescriptorFactory}.
 * As such it is able to be configured using a {@link StandardArooaParser}.
 *
 * @oddjob.description A definition of an Arooa descriptor.
 * 
 * @oddjob.example
 * 
 * See the Dev Guide. There is an example of a custom descriptor
 * <a href="http://rgordon.co.uk/projects/oddjob/devguide/oddballs.html">here</a>.
 * 
 * @oddjob.example
 * 
 * The descriptor for the JMX client and server. This is the internal descriptor 
 * used by Oddjob.
 * 
 * {@oddjob.xml.resource org/oddjob/jmx/jmx.xml}
 *  
 * @author rob
 *
 */
public class ArooaDescriptorBean 
implements ArooaDescriptorFactory {
	
	/** 
     * @oddjob.property
     * @oddjob.description The name space that applies to 
     * all elements defined in definitions.
     * @oddjob.required No.
	 */
	private URI namespace;
	
	/** 
     * @oddjob.property
     * @oddjob.description The default prefix for the name space.
     * @oddjob.required Yes if name space is provided.
	 */
	private String prefix;
	
	/** 
     * @oddjob.property conversions 
     * @oddjob.description List of class names that must implement
     * the {@link ConversionProvider} interface.
     * @oddjob.required No.
	 */
	private List<String> convertlets = 
		new ArrayList<String>();
	
	/** 
     * @oddjob.property 
     * @oddjob.description A list of {@link BeanDefinition}s for components.
     * @oddjob.required No.
	 */
	private List<BeanDefinition> components = 
		new ArrayList<BeanDefinition>();
	
	/** 
     * @oddjob.property 
     * @oddjob.description A list of {@link BeanDefinition}s for values.
     * @oddjob.required No.
	 */
	private List<BeanDefinition> values = 
		new ArrayList<BeanDefinition>();
		
	/**
	 * Setter for conversions.
	 * 
	 * @param index
	 * @param convertletProvider
	 */
	public void setConversions(int index, 
			String convertletProvider) {
		convertlets.add(index, convertletProvider);
	}

	/**
	 * Setter for components.
	 * 
	 * @param components
	 */
	public void setComponents(int index, BeanDefinition component) {
		new ListSetterHelper<BeanDefinition>(this.components).set(index, component);
	}
	
	/**
	 * Setter for values.
	 * 
	 * 
	 * @param value
	 */
	public void setValues(int index, BeanDefinition value)	{
		new ListSetterHelper<BeanDefinition>(this.values).set(index, value);
	}
	
	/**
	 * Internal class to hold mappings.
	 * 
	 */
	static class Mappings implements ElementMappings {
		
		private final Map<ArooaElement, SimpleArooaClass> mappings =
			new LinkedHashMap<ArooaElement, SimpleArooaClass>();
			
		private final Map<ArooaElement, DesignFactory> designs =
			new LinkedHashMap<ArooaElement, DesignFactory>();
		
		@Override
		public ArooaClass mappingFor(ArooaElement element, 
				InstantiationContext parentContext) {

			SimpleArooaClass identifier = mappings.get(element);
			if (identifier == null) {
				return null;
			}
			return identifier;
		}
		
		@Override
		public DesignFactory designFor(ArooaElement element, 
				InstantiationContext parentContext) {

			return designs.get(element);
		}
		
		@Override
		public ArooaElement[] elementsFor(
				InstantiationContext propertyContext) {
			return new ElementsForIdentifier(
					mappings).elementsFor(propertyContext);
		}
		
		@Override
		public MappingsContents getBeanDoc(ArooaType arooaType) {
			return new MappingsBeanDoc(mappings);
		}
	}
	
	private void populateMappings(Mappings mappings, 
			BeanDefinitions definitions,
			Map<ArooaClass, BeanDefinition> definitionsMap,
			ClassLoader classLoader) {

		URI namespace = definitions.getNamespace();
		
		for (BeanDefinition beanDefinition : definitions.getDefinitions()) {
			
			if (beanDefinition.getElement() == null) {
				throw new ArooaException("No Element in Class Mappings.");
			}
			
			ArooaElement element = new ArooaElement(
					namespace,
					beanDefinition.getElement()); 
			
			SimpleArooaClass classIdentifier = null;
				
			try {
				Class<?> theClass = ClassesUtils.classFor(
						beanDefinition.getClassName(),
						classLoader);

				classIdentifier = new SimpleArooaClass(theClass);
				
				mappings.mappings.put(element, classIdentifier);
			} 
			catch (ClassNotFoundException e) {
				throw new ArooaException("Failed loading class for element " +
						element + " class [" + beanDefinition.getClassName() +
						"] using class loader [" + classLoader + "]",
						e);
			}
			
			if (beanDefinition.getDesignFactory() != null) {
				
				try {
					mappings.designs.put(element, 
							(DesignFactory) ClassesUtils.classFor(
									beanDefinition.getDesignFactory(),
									classLoader).newInstance());
				}
				catch (Exception e) {
					throw new ArooaException("Failed loading design class for element " +
							element + " class [" + beanDefinition.getClassName() +
							"] using class loader [" + classLoader + "]",
							e);
				}
			}
			
			definitionsMap.put(classIdentifier, beanDefinition);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.deploy.ArooaDescriptorFactory#createDescriptor(java.lang.ClassLoader)
	 */
	public ArooaDescriptor createDescriptor(final ClassLoader classLoader) {

		final Map<ArooaClass, BeanDefinition> beanDefinitions = 
			new HashMap<ArooaClass, BeanDefinition>();
		
		final Map<ArooaClass, ArooaBeanDescriptor> beanDescriptors = 
				new HashMap<ArooaClass, ArooaBeanDescriptor>();
		
		final Mappings componentMappings= new Mappings();
			
		BeanDefinitions components = new BeanDefinitions(
				namespace, prefix, this.components);
			
		populateMappings(componentMappings, components, 
				beanDefinitions,
				classLoader);
		
		final Mappings valueMappings = new Mappings();
			
		BeanDefinitions values = new BeanDefinitions(
				namespace, prefix, this.values);

		populateMappings(valueMappings, values, 
				beanDefinitions,
				classLoader);

		return new ArooaDescriptor() {

			@Override
			public ElementMappings getElementMappings() {
				return new MappingsSwitch(componentMappings, valueMappings);
			}

			@Override
			public ArooaBeanDescriptor getBeanDescriptor(
					ArooaClass forClass, PropertyAccessor accessor) {

				ArooaBeanDescriptor beanDescriptor = 
						beanDescriptors.get(forClass);

				if (beanDescriptor != null) {
					return beanDescriptor;
				}
				
				BeanDefinition beanDefinition = 
					beanDefinitions.get(forClass);
				
				if (beanDefinition == null) {
					return null;
				}
								
				beanDescriptor = new SupportedBeanDescriptorProvider(
							).getBeanDescriptor(
									forClass, accessor);
				
				if (beanDescriptor instanceof PropertyDefinitionsHelper) {

					((PropertyDefinitionsHelper) 
							beanDescriptor).mergeFromBeanDefinition(
									beanDefinition);
				}
				
				beanDescriptors.put(forClass, beanDescriptor);
				
				return beanDescriptor;
			}

			@Override
			public ConversionProvider getConvertletProvider() {
				return new ConversionProvider() {
					
					public void registerWith(ConversionRegistry registry) {
						for (String className: convertlets) {
							if (className == null) {
								continue;
							}
							ConversionProvider convertletProvider = (ConversionProvider)
								ClassesUtils.instantiate(className, classLoader);
							convertletProvider.registerWith(registry);
						}
					}
				};
			}
			
			@Override
			public String getPrefixFor(URI namespace) {
				if (namespace == null) {
					return null;
				}
				
				if (namespace.equals(getNamespace())) {
					return getPrefix();
				}
				
				return null;
			}

			@Override
			public ClassResolver getClassResolver() {
				return new ClassLoaderClassResolver(classLoader);
			}
		};
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
	
}
