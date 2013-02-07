package org.oddjob.arooa.beanutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.beanutils.MagicBeanDescriptorProperty.PropertyType;
import org.oddjob.arooa.deploy.BeanDescriptorProvider;
import org.oddjob.arooa.deploy.DefaultBeanDescriptorProvider;
import org.oddjob.arooa.deploy.PropertyDefinitionsHelper;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * @oddjob.description Definition for a Magic Bean, which is a bean that can be defined 
 * dynamically.
 * 
 * @author rob
 *
 */
public class MagicBeanDefinition {
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The name of the element. 
	 * @oddjob.required Yes.
	 */
	private String element;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The bean properties. This is a list
	 * of {@link MagicBeanDescriptorProperty}s.
	 * @oddjob.required No.
	 */
	private final List<MagicBeanDescriptorProperty> properties = 
		new ArrayList<MagicBeanDescriptorProperty>();
	
	public String getElement() {
		return element;
	}

	public void setElement(String name) {
		this.element = name;
	}

	public void setProperties(int index, MagicBeanDescriptorProperty property) {
		
		if (property == null) {
			properties.remove(index);
		}
		else {
			properties.add(index, property);
		}
	}
	
	public MagicBeanDescriptorProperty getProperties(int index) {
		return properties.get(index);
	}
	
	public ArooaClass createMagic(ClassLoader loader) {

		MagicBeanClassCreator classCreator = new MagicBeanClassCreator(
				"DescriptorMagicBean-" + element);
		
		for (MagicBeanDescriptorProperty prop : properties) {
						
			String className = prop.getType();
			Class<?> cl;
			if (className == null) {
				cl = String.class;
			}
			else {
				try {
					cl = Class.forName(className, true, loader);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("For MagicBean class " + 
							element + ", property " + prop.getName(), e);
				}
			}
			
			classCreator.addProperty(prop.getName(), cl);
		}
				
		return classCreator.create();
	}
	
	/**
	 * Create a {@link BeanDescriptorProvider} based on how the property
	 * is configured.
	 * 
	 * @return
	 */
	public BeanDescriptorProvider createMagicBeanDescriptorProvider() {

		Configurings configurings = new Configurings();
		for (MagicBeanDescriptorProperty prop : properties) {
			if (prop.getConfigured() != null) {
				configurings.add(prop.getName(), prop.getConfigured());
			}
		}
		
		return configurings;
	}
	
	static class Configurings implements BeanDescriptorProvider {
		
		private String textProperty;
		
		private final Map<String, ConfiguredHow> conifiguredHowByProperty = 
				new HashMap<String, ConfiguredHow>();
		
		void add(String property, PropertyType type) {
			switch(type) {
			case ATTRIBUTE:
				conifiguredHowByProperty.put(property, 
						ConfiguredHow.ATTRIBUTE);
				break;
			case ELEMENT:
				conifiguredHowByProperty.put(property, 
						ConfiguredHow.ELEMENT);
				break;
			case TEXT:
				textProperty = property;
				break;
			}
		}

		@Override
		public ArooaBeanDescriptor getBeanDescriptor(ArooaClass arooaClass,
				PropertyAccessor accessor) {
		
			final PropertyDefinitionsHelper defaultBeanDescriptor = 
					new DefaultBeanDescriptorProvider().getBeanDescriptor(
							arooaClass, accessor);
			
			return new ArooaBeanDescriptor() {
				
				@Override
				public boolean isAuto(String property) {
					return false;
				}
				
				@Override
				public String getTextProperty() {
					return textProperty;
				}
				
				@Override
				public ParsingInterceptor getParsingInterceptor() {
					return null;
				}
				
				@Override
				public String getFlavour(String property) {
					return null;
				}
				
				@Override
				public ConfiguredHow getConfiguredHow(String property) {
					ConfiguredHow how = 
							conifiguredHowByProperty.get(property);
					
					if (how != null) {
						return how;
					}
					
					return defaultBeanDescriptor.getConfiguredHow(property);
				}
				
				@Override
				public String getComponentProperty() {
					return null;
				}
				
				@Override
				public ArooaAnnotations getAnnotations() {
					return null;
				}
			};
		}
	}

}
