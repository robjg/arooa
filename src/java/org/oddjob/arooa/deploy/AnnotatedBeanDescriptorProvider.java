package org.oddjob.arooa.deploy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.inject.Named;

import org.oddjob.arooa.ArooaBeanDescriptor;
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
public class AnnotatedBeanDescriptorProvider implements BeanDescriptorProvider {
	
	public PropertyDefinitionsHelper getBeanDescriptor(
			ArooaClass classIdentifier, PropertyAccessor accessor) {
		
		Class<?> cl = classIdentifier.forClass();
		
		PropertyDefinitionsHelper beanDescriptor = 
				new DefaultBeanDescriptorProvider().getBeanDescriptor(
						classIdentifier, accessor);
		
		ArooaInterceptor annotation = cl.getAnnotation(ArooaInterceptor.class);
		if (annotation != null) {

			String interceptor = annotation.value();
			if (interceptor.length() > 0) {
				ParsingInterceptor parsingInterceptor = (ParsingInterceptor)
				ClassesUtils.instantiate(
						interceptor, cl.getClassLoader());
				beanDescriptor.setParsingInterceptor(parsingInterceptor);				
			}
		}

		MemberAnnotationFinder finder = new MemberAnnotationFinder(cl);
		
		DefinitionResults results = new DefinitionResults(beanDescriptor);
		
		finder.find(results);

		return beanDescriptor;
	}

	/**
	 * Save {@link PropertyDefintion}s for discovered annotations.
	 *
	 */
	class DefinitionResults implements AnnotationResults {
		
		private final PropertyDefinitionsHelper base;
		
		public DefinitionResults(PropertyDefinitionsHelper base) {
			this.base = base;
		}
				
		public void attributeProperty(String name) {
			base.setPropertyType(name, 
					PropertyDefinition.PropertyType.ATTRIBUTE);
		}
		public void elementProperty(String name) {
			base.setPropertyType(name, 
					PropertyDefinition.PropertyType.ELEMENT);
		}
		public void hiddenProperty(String name) {
			base.setPropertyType(name, 
					PropertyDefinition.PropertyType.HIDDEN);
		}
		public void componentProperty(String name) {
			base.setPropertyType(name, 
					PropertyDefinition.PropertyType.COMPONENT);
		}
		public void textProperty(String name) {
			base.setPropertyType(name, 
					PropertyDefinition.PropertyType.TEXT);
		}
		public void auto(String name, boolean value) {
			base.setAuto(name);
		}
		public void flavour(String name, String flavour) {
			base.setFlavour(name, flavour);
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
