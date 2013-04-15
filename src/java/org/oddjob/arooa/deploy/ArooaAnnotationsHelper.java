package org.oddjob.arooa.deploy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * A simple implementation {@link ArooaAnnotations}.
 * 
 * @author rob
 *
 */
public class ArooaAnnotationsHelper implements ArooaAnnotations {

	private final Class<?> theClass;

	private final Map<String, List<Method>> methodsFor = 
			new HashMap<String, List<Method>>();

	private final Map<String, Map<String, ArooaAnnotation>> propertyAnnotations = 
			new HashMap<String, Map<String, ArooaAnnotation>>();
	
	/**
	 * Constructor.
	 * 
	 * @param classIdentifier
	 */
	public ArooaAnnotationsHelper(ArooaClass classIdentifier) {

		this.theClass = classIdentifier.forClass();

		for (Method method : theClass.getMethods()) {

			Annotation[] annotations = method.getAnnotations();

			for (Annotation annotation : annotations) {
				
				addMethod(new AnnotationArooaAnnotation(annotation), method);
			}
		}
		
		for (Class<?> cl = theClass; cl != null; 
				cl = cl.getSuperclass()) {
			for (Field field : cl.getDeclaredFields()) {

				Annotation[] annotations = field.getAnnotations();

				for (Annotation annotation : annotations) {
					
					addProperty(new AnnotationArooaAnnotation(annotation), 
							field.getName());
				}
			}
		}
	}
	
	private void addMethod(ArooaAnnotation annotation, Method method) {

		String annotationName = 
				annotation.getName();
		
		List<Method> methods = methodsFor.get(annotationName);
		if (methods == null) {
			methods = new ArrayList<Method>();
			this.methodsFor.put(annotationName, methods);
		}
		methods.add(method);
		
		maybeAddProperty(annotation, method);
	}

	private void maybeAddProperty(ArooaAnnotation annotation, Method method) {
		
		String methodName = method.getName();
		if (!methodName.startsWith("set") && 
				!methodName.startsWith("get")) {
			return;
		}
		
		String property = method.getName().substring(3);

		if (property.length() == 0) {
			return;
		}
		
		property = property.substring(0, 1).toLowerCase() + 
				(property.length() == 1 ? "" : 
					property.substring(1));
		
		addProperty(annotation, property);
	}

	private void addProperty(ArooaAnnotation annotation, String property) {
		
		String annotationName = 
				annotation.getName();
				
		Map<String, ArooaAnnotation> set = propertyAnnotations.get(property);
		
		if (set == null) {
			set = new HashMap<String, ArooaAnnotation>(); 
			propertyAnnotations.put(property, set);
		}
		
		set.put(annotationName, annotation);
	}
	
	/**
	 * Add a property definition.
	 * 
	 * @param definition
	 */
	public void addPropertyDefinition(PropertyDefinition definition) {
		
		String annotation = definition.getAnnotation();
		
		if (annotation == null) {
			return;
		}

		addProperty(new SyntheticArooaAnnotation(annotation), 
				definition.getName());
	}
	
	/**
	 * Add an annotation definition.
	 * 
	 * @param defintion
	 */
	public void addAnnotationDefintion(AnnotationDefinition defintion) {
		try {
			String parameterTypeList = defintion.getParameterTypes();
			Class<?>[] parameterTypes;
			if (parameterTypeList == null) {
				parameterTypes = new Class<?>[0];
			} else {
				String[] types = parameterTypeList.split("\\s*,\\s*");
				parameterTypes = new Class<?>[types.length];
				for (int i = 0; i < types.length; ++i)
					parameterTypes[i] = Class.forName(types[i], true,
							theClass.getClassLoader());
			}

			Method method = theClass.getMethod(defintion.getMethod(),
					parameterTypes);

			addMethod(new SyntheticArooaAnnotation(defintion.getName()), 
					method);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Method methodFor(String annotationName) {

		List<Method> methods = methodsFor.get(annotationName);
		if (methods == null) {
			return null;
		}
		
		if (methods.size() > 1) {
			throw new IllegalStateException("More than one method for " +
						annotationName);
		}
		else {
			return methods.get(0);
		}
	}
	
	@Override
	public String[] annotatedProperties() {
		Set<String> keys = propertyAnnotations.keySet(); 
		return keys.toArray(new String[keys.size()]);
	}
	
	@Override
	public ArooaAnnotation[] annotationsForProperty(String propertyName) {
		Map<String, ArooaAnnotation> annotations = propertyAnnotations.get(propertyName);
		if (annotations == null) {
			return new ArooaAnnotation[0];
		}
		
		Collection<ArooaAnnotation> values = annotations.values();
		
		return values.toArray(new ArooaAnnotation[values.size()]);
	}
	
	@Override
	public ArooaAnnotation annotationForProperty(String propertyName,
			String annotationName) {
		Map<String, ArooaAnnotation> annotations = propertyAnnotations.get(propertyName);
		if (annotations == null) {
			return null;
		}
		return annotations.get(annotationName);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + 
				": num method annotations=" + methodsFor.size() + 
				", num property annotations=" + propertyAnnotations.size();
	}	
}
