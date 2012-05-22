package org.oddjob.arooa.deploy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private final Map<String, List<Method>> methodsFor = new HashMap<String, List<Method>>();

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
				add(annotation.annotationType().getName(), method);
			}
		}
	}

	private void add(String annotationName, Method method) {
		List<Method> methods = methodsFor.get(annotationName);
		if (methods == null) {
			methods = new ArrayList<Method>();
			this.methodsFor.put(annotationName, methods);
		}
		methods.add(method);
	}

	/**
	 * Add an annotation definition.
	 * 
	 * @param defintion
	 */
	public void add(AnnotationDefinition defintion) {
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

			Method method = theClass.getDeclaredMethod(defintion.getMethod(),
					parameterTypes);

			add(defintion.getName(), method);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

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
}
