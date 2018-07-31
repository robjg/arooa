package org.oddjob.arooa.deploy;

import javax.inject.Inject;
import javax.inject.Named;

import org.oddjob.arooa.ArooaAnnotations;
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
import org.oddjob.arooa.utils.ClassUtils;

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
				ClassUtils.instantiate(
						interceptor, cl.getClassLoader());
				beanDescriptor.setParsingInterceptor(parsingInterceptor);				
			}
		}

		ArooaAnnotations annotations = beanDescriptor.getAnnotations();

		String[] properties = annotations.annotatedProperties();
		
		for (String property : properties) {
			
			ArooaAnnotation arooaAnnotation;
			
			arooaAnnotation = annotations.annotationForProperty(
					property, ArooaText.class.getName());
			
			if (arooaAnnotation != null) {
				beanDescriptor.setPropertyType(property, 
						PropertyDefinition.PropertyType.TEXT);
			}
			
			arooaAnnotation = annotations.annotationForProperty(
					property, ArooaComponent.class.getName());
			
			if (arooaAnnotation != null) {
				beanDescriptor.setPropertyType(property, 
						PropertyDefinition.PropertyType.COMPONENT);
			}

			
			arooaAnnotation = annotations.annotationForProperty(
					property, ArooaAttribute.class.getName());
			
			if (arooaAnnotation != null) {
				beanDescriptor.setPropertyType(property, 
						PropertyDefinition.PropertyType.ATTRIBUTE);
			}

			
			arooaAnnotation = annotations.annotationForProperty(
					property, ArooaElement.class.getName());
			
			if (arooaAnnotation != null) {
				beanDescriptor.setPropertyType(property, 
						PropertyDefinition.PropertyType.ELEMENT);
			}

			arooaAnnotation = annotations.annotationForProperty(
					property, ArooaHidden.class.getName());
			
			if (arooaAnnotation != null) {
				beanDescriptor.setPropertyType(property, 
						PropertyDefinition.PropertyType.HIDDEN);
			}

			arooaAnnotation = annotations.annotationForProperty(
					property, Named.class.getName());
			
			if (arooaAnnotation != null) {
				Named named = arooaAnnotation.realAnnotation(Named.class);
				if (named == null) {
					throw new IllegalArgumentException(
							"NAMED must be a real annotation.");
				}
				
				beanDescriptor.setFlavour(property, named.value());
				
			}
			
			arooaAnnotation = annotations.annotationForProperty(
					property, Inject.class.getName());
			
			if (arooaAnnotation != null) {
				beanDescriptor.setAuto(property);
			}
			
		}
		
		return beanDescriptor;
	}

}
