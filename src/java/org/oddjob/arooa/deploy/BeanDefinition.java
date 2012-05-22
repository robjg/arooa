package org.oddjob.arooa.deploy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.oddjob.arooa.ParsingInterceptor;

/**
 * @oddjob.description Provide an element to class name mapping for a 
 * java bean. Additionally allows an {@link ArooaBeanDescriptor} to be
 * provided for the class by specifying additional {@link PropertyDefinition}s.
 * 
 * @author rob
 *
 */
public class BeanDefinition {
	
	/** 
     * @oddjob.property interceptor
     * @oddjob.description A ParsingInterceptor. This
     * will change to a class name in future releases.
     * @oddjob.required No.
	 */
	private ParsingInterceptor parsingInterceptor;

	/** 
     * @oddjob.property
     * @oddjob.description The unqualified element name for the
     * mapping.
     * @oddjob.required No, if this definition is only providing
     * an ArooaBeanDescriptor.
	 */
	private String element;
	
	/** 
     * @oddjob.property
     * @oddjob.description The class name for the bean.
     * @oddjob.required Yes.
	 */
	private String className;
	
	/** 
     * @oddjob.property
     * @oddjob.description A class name that provides a 
     * DesignFactory for the bean.
     * @oddjob.required No.
	 */
	private String designFactory;
	
	/** 
     * @oddjob.property
     * @oddjob.description A list of {@link PropertyDefinition}s
     * @oddjob.required No.
	 */
	private final List<PropertyDefinition> properties = 
		new ArrayList<PropertyDefinition>();
	
	/** 
     * @oddjob.property
     * @oddjob.description A list of {@link AnnotationDefinition}.
     * @oddjob.required No.
	 */
	private final List<AnnotationDefinition> annotations = 
		new ArrayList<AnnotationDefinition>();
	
	/**
	 * Setter for interceptor.
	 * 
	 * @param parsingInterceptor
	 */
	public void setInterceptor(ParsingInterceptor parsingInterceptor) {
		this.parsingInterceptor = parsingInterceptor;
	}
	
	/**
	 * Getter for interceptor.
	 * 
	 * @return
	 */
	public ParsingInterceptor getInterceptor() {
		return parsingInterceptor;
	}

	/**
	 * Getter for class name;
	 * 
	 * @return
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Setter for class name.
	 * 
	 * @param className
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Getter for design factory.
	 * 
	 * @return
	 */
	public String getDesignFactory() {
		return designFactory;
	}

	/**
	 * Setter for design factory.
	 * 
	 * @param designFactory
	 */
	public void setDesignFactory(String designFactory) {
		this.designFactory = designFactory;
	}

	/**
	 * Getter for element.
	 * 
	 * @return
	 */
	public String getElement() {
		return element;
	}

	/**
	 * Setter for element
	 * 
	 * @param element
	 */
	public void setElement(String element) {
		this.element = element;
	}
	
	/**
	 * Setter for property definitions.
	 *  
	 * @param index
	 * @param property
	 */
	public void setProperties(int index, PropertyDefinition property) {
		if (property == null) {
			properties.remove(index);
		}
		else {	
			properties.add(index, property);
		}
	}

	/**
	 * Convert the property definitions to a collection.
	 * 
	 * @return
	 */
	public Collection<PropertyDefinition> toPropertyDefinitions() {
		return properties;
	}
	
	/**
	 * Setter for annotations.
	 * 
	 * @param index
	 * @param annotation
	 */
	public void setAnnotations(int index, AnnotationDefinition annotation) {
		if (annotation == null) {
			annotations.remove(index);
		}
		else {
			annotations.add(index, annotation);
		}
	}

	/**
	 * Convert annotations to a collection.
	 * @return
	 */
	public Collection<AnnotationDefinition> toAnnotationDefinitions() {
		return annotations;
	}
	
	
	boolean isArooaBeanDescriptor() {
		
		if (parsingInterceptor != null) {
			return true;
		}
		
		if (properties.size() > 0) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ", element=" + element;
	}
}

