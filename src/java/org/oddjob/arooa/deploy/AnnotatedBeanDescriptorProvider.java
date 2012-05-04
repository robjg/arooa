package org.oddjob.arooa.deploy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.deploy.annotations.ArooaElement;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.deploy.annotations.ArooaInterceptor;
import org.oddjob.arooa.deploy.annotations.ArooaText;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.utils.ClassesUtils;

/**
 * Attempts to provide a {@link ArooaBeanDescriptor} from an annotated
 * class.
 * 
 * @author rob
 *
 */
public class AnnotatedBeanDescriptorProvider {
	
	public ArooaBeanDescriptor getBeanDescriptor(
			ArooaClass classIdentifier, PropertyAccessor accessor) {
		
		Class<?> cl = classIdentifier.forClass();
		
		BeanDefinition beanDescriptor = 
			new BeanDefinition();
		
		beanDescriptor.setClassName(cl.getName());
		
		ArooaInterceptor annotation = cl.getAnnotation(ArooaInterceptor.class);
		if (annotation != null) {

			String interceptor = annotation.value();
			if (interceptor.length() > 0) {
				ParsingInterceptor parsingInterceptor = (ParsingInterceptor)
				ClassesUtils.instantiate(
						interceptor, cl.getClassLoader());
				beanDescriptor.setInterceptor(parsingInterceptor);				
			}
		}

		MemberAnnotationFinder finder = new MemberAnnotationFinder(cl);
		
		DefinitionResults results = new DefinitionResults(classIdentifier,
				accessor);
		
		finder.find(results);

		results.addTo(beanDescriptor);
		
		if (beanDescriptor.isArooaBeanDescriptor()) {
			PropertyDefinitionsHelper compositeDescriptor = 
				new DefaultBeanDescriptorProvider().getBeanDescriptor(
								classIdentifier, accessor);
			
			compositeDescriptor.mergeFromBeanDefinition(beanDescriptor);
			
			return compositeDescriptor;
		}
		else {
			return null;
		}
	}

	/**
	 * Save {@link PropertyDefintion}s for discovered annotations.
	 *
	 */
	class DefinitionResults implements AnnotationResults {
		
		private final ArooaBeanDescriptor base;
		
		private final Map<String, PropertyDefinition> defs = 
			new LinkedHashMap<String, PropertyDefinition>();

		private final ArooaClass cl;
		
		public DefinitionResults(ArooaClass cl, 
				PropertyAccessor accessor) {
			this.cl = cl;
			this.base = 
				new DefaultBeanDescriptorProvider(
						).getBeanDescriptor(cl, accessor);
		}
		
		/**
		 * Provide or create a property definition.
		 * 
		 * @return PropertyDefinition
		 */
		private PropertyDefinition forName(String property) {
			PropertyDefinition def = defs.get(property);
			if (def == null) {
				ConfiguredHow how = base.getConfiguredHow(property); 
				if (how == null) {
					throw new NullPointerException(
							"No configuration information for property [" + 
							property + "] of class [" + cl + 
							"] - Is this property defined correctly?");
				}
				def = new PropertyDefinition(property, propertyType(how));
				defs.put(property, def);
			}
			return def;
		}
		
		private PropertyDefinition.PropertyType propertyType(
				ConfiguredHow how) {
			switch (how) {
			case ATTRIBUTE:
				return PropertyDefinition.PropertyType.ATTRIBUTE;
			default:
				return PropertyDefinition.PropertyType.ELEMENT;
			}
		}
		
		public void attributeProperty(String name) {
			forName(name).setType(PropertyDefinition.PropertyType.ATTRIBUTE);
		}
		public void elementProperty(String name) {
			forName(name).setType(PropertyDefinition.PropertyType.ELEMENT);
		}
		public void hiddenProperty(String name) {
			forName(name).setType(PropertyDefinition.PropertyType.HIDDEN);
		}
		public void componentProperty(String name) {
			forName(name).setType(PropertyDefinition.PropertyType.COMPONENT);
		}
		public void textProperty(String name) {
			forName(name).setType(PropertyDefinition.PropertyType.TEXT);
		}
		public void auto(String name, boolean value) {
			forName(name).setAuto(value);
		}
		public void flavour(String name, String flavour) {
			forName(name).setFlavour(flavour);
		}
		
		void addTo(BeanDefinition beanDescriptor) {
			for (PropertyDefinition def : defs.values()) {
				beanDescriptor.setProperties(0, def);
			}
		}

	}
	
	/**
	 * Something that can process discovered annotations.
	 */
	interface AnnotationResults {

		void componentProperty(String name);
		
		void textProperty(String name);
		
		void attributeProperty(String name);
		
		void elementProperty(String name);
		
		void hiddenProperty(String name);
		
		void auto(String name, boolean value);
		
		void flavour(String name, String flavour);
	}
	
	/**
	 * Finds annotations by iterating over methods only.
	 *
	 */
	static class MemberAnnotationFinder {
	
		private final Class<?> cl;
		
		public MemberAnnotationFinder(Class<?> cl) {
			this.cl = cl;
		}
		
		/**
		 * Look for annotations and populate the results.
		 *  
		 * @param results The results to be populated.
		 */
		public void find(AnnotationResults results) {
			
			for (Method method : cl.getMethods()) {

				if (!method.getName().startsWith("set")) {
					continue;
				}
				
				String property = method.getName().substring(3);

				if (property.length() == 0) {
					continue;
				}
				
				property = property.substring(0, 1).toLowerCase() + 
					property.substring(1);
					
				Annotation[] annotations = method.getAnnotations();
				
				for (Annotation arooaAnnotation: annotations) {
					
					if (arooaAnnotation instanceof ArooaComponent) {
						results.componentProperty(property);
					}
					else if (arooaAnnotation instanceof ArooaText) {
						results.textProperty(property);
					}
					else if (arooaAnnotation instanceof ArooaAttribute) {
						results.attributeProperty(property);
					}
					else if (arooaAnnotation instanceof ArooaElement) {
						results.elementProperty(property);
					}
					else if (arooaAnnotation instanceof ArooaHidden) {
						results.hiddenProperty(property);
					}
					else if (arooaAnnotation instanceof Named) {
						results.flavour(property, ((Named) arooaAnnotation).value());
					}
					else if (arooaAnnotation instanceof Inject) {
						results.auto(property, true);
					}
				}
			}
		}
	}
}
