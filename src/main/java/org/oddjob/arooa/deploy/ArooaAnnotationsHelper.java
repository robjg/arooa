package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaError;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.utils.ClassUtils;
import org.oddjob.arooa.utils.EtcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Helps build an {@link org.oddjob.arooa.ArooaBeanDescriptor} from annotations
 * and provides it with the {@link ArooaAnnotations} view of annotations.
 *
 * @see AnnotatedBeanDescriptorContributor
 *
 * @author rob
 *
 */
public class ArooaAnnotationsHelper {

	private static final Logger logger = LoggerFactory.getLogger(ArooaAnnotationsHelper.class);

	private final Class<?> theClass;

	private final Map<String, List<Method>> methodsFor =
			new HashMap<>();

	private final Map<String, Map<String, ArooaAnnotation>> propertyAnnotations =
			new HashMap<>();
	
	/**
	 * Constructor.
	 * 
	 * @param classIdentifier
	 */
	public ArooaAnnotationsHelper(ArooaClass classIdentifier) {

		try {
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
		catch (Error e) {
			throw new ArooaError("Failed analyzing annotations for " + classIdentifier, e);
		}
	}
	
	private void addMethod(ArooaAnnotation annotation, Method method) {

		String annotationName = 
				annotation.getName();
		
		List<Method> methods = methodsFor.get(annotationName);
		if (methods == null) {
			methods = new ArrayList<>();
			this.methodsFor.put(annotationName, methods);
		}
		methods.add(method);
		
		maybeAddProperty(annotation, method);
	}

	private void maybeAddProperty(ArooaAnnotation annotation, Method method) {

		EtcUtils.propertyFromMethodName(method.getName())
				.ifPresent(property -> addProperty(annotation, property));
	}

	private void addProperty(ArooaAnnotation annotation, String property) {
		
		String annotationName = 
				annotation.getName();

		propertyAnnotations.computeIfAbsent(property, k -> new HashMap<>())
				.put(annotationName, annotation);
	}
	
	/**
	 * Add a property definition.
	 * 
	 * @param definition
	 */
	public void addPropertyDefinition(PropertyDefinitionBean definition) {
		
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
	 * @param definition The annotation definition.
	 */
	public void addAnnotationDefinition(AnnotationDefinitionBean definition) {
		try {
			String parameterTypeList = definition.getParameterTypes();
			Class<?>[] parameterTypes;
			if (parameterTypeList == null) {
				parameterTypes = new Class<?>[0];
			} else {
				String[] types = parameterTypeList.split("\\s*,\\s*");
				parameterTypes = new Class<?>[types.length];
				for (int i = 0; i < types.length; ++i)
					parameterTypes[i] = ClassUtils.classFor(types[i],
							theClass.getClassLoader());
			}

			Method method = theClass.getMethod(definition.getMethod(),
					parameterTypes);

			addMethod(new SyntheticArooaAnnotation(definition.getName()),
					method);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String[] annotatedProperties() {
		Set<String> keys = propertyAnnotations.keySet(); 
		return keys.toArray(new String[0]);
	}
	
	public ArooaAnnotation[] annotationsForProperty(String propertyName) {
		Map<String, ArooaAnnotation> annotations = propertyAnnotations.get(propertyName);
		if (annotations == null) {
			return new ArooaAnnotation[0];
		}
		
		Collection<ArooaAnnotation> values = annotations.values();
		
		return values.toArray(new ArooaAnnotation[0]);
	}
	
	public ArooaAnnotation annotationForProperty(String propertyName,
			String annotationName) {
		Map<String, ArooaAnnotation> annotations = propertyAnnotations.get(propertyName);
		if (annotations == null) {
			return null;
		}
		return annotations.get(annotationName);
	}

	public ArooaAnnotations toArooaAnnotations() {
		return new ImmutableImpl(this);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + 
				": num method annotations=" + methodsFor.size() + 
				", num property annotations=" + propertyAnnotations.size();
	}

	static class ImmutableImpl implements ArooaAnnotations {

		private final Class<?> theClass;

		private final Map<String, List<Method>> methodsFor =
				new HashMap<>();

		private final Map<String, List<String>> propertiesFor =
				new HashMap<>();

		private final Map<String, Map<String, ArooaAnnotation>> propertyAnnotations =
				new HashMap<>();

		ImmutableImpl(ArooaAnnotationsHelper helper) {

			theClass = helper.theClass;

			for (Map.Entry<String, List<Method>> entry : helper.methodsFor.entrySet()) {
				methodsFor.put(entry.getKey(), new ArrayList<>(entry.getValue()));
			}

			for (Map.Entry<String, Map<String, ArooaAnnotation>> entry :
					helper.propertyAnnotations.entrySet() ) {
				propertyAnnotations.put(entry.getKey(), new HashMap<>(entry.getValue()));
				for (String annotationName : entry.getValue().keySet() ) {
					propertiesFor.computeIfAbsent(annotationName, k -> new ArrayList<>())
							.add(entry.getKey());
				}
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
						annotationName + ": " + methods);
			}
			else {
				return methods.get(0);
			}
		}

		@Override
		public String propertyFor(String annotationName) {
			List<String> properties = propertiesFor.get(annotationName);
			if (properties == null) {
				return null;
			}

			if (properties.size() > 1) {
				throw new IllegalStateException("More than one property for " +
						annotationName + ": " + properties);
			}
			else {
				return properties.get(0);
			}
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
			return ArooaAnnotations.class.getSimpleName() + " for " + theClass +
					": num method annotations=" + methodsFor.size() +
					", num property annotations=" + propertiesFor.size();
		}

	}

}
