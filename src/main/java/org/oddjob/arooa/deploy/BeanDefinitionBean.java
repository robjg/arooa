package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.design.DesignFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @oddjob.description Provide an element to class name mapping for a 
 * java bean. Additionally, allows an {@link ArooaBeanDescriptor} to be
 * provided for the class by specifying additional {@link PropertyDefinitionBean}s.
 * 
 * @author rob
 *
 */
public class BeanDefinitionBean implements BeanDefinition {
	
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
	 * @oddjob.description A class name that provides a
	 * DesignFactory for the bean.
	 * @oddjob.required No.
	 */
	private DesignFactory design;

	/**
     * @oddjob.property
     * @oddjob.description A list of {@link PropertyDefinitionBean}s
     * @oddjob.required No.
	 */
	private final List<PropertyDefinitionBean> properties =
			new ArrayList<>();
	
	/** 
     * @oddjob.property
     * @oddjob.description A list of {@link AnnotationDefinitionBean}.
     * @oddjob.required No.
	 */
	private final List<AnnotationDefinitionBean> annotations =
			new ArrayList<>();
	
	/**
	 * Setter for interceptor.
	 * 
	 * @param parsingInterceptor The interceptor
	 */
	public void setInterceptor(ParsingInterceptor parsingInterceptor) {
		this.parsingInterceptor = parsingInterceptor;
	}
	
	/**
	 * Getter for interceptor.
	 * 
	 * @return The interceptor.
	 */
	public ParsingInterceptor getInterceptor() {
		return parsingInterceptor;
	}

	/**
	 * Getter for class name;
	 * 
	 * @return The class name.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Setter for class name.
	 * 
	 * @param className The class name.
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Getter for design factory.
	 * 
	 * @return The design factory.
	 */
	public String getDesignFactory() {
		return designFactory;
	}

	/**
	 * Setter for design factory.
	 * 
	 * @param designFactory The design factory.
	 */
	public void setDesignFactory(String designFactory) {
		this.designFactory = designFactory;
	}

	/**
	 * Getter for Design.
	 *
	 * @return A Design Factory or null.
	 */
	public DesignFactory getDesign() {
		return design;
	}

	/**
	 * Setter for Design.
	 *
	 * @param design A Design Factory or null.
	 */
	public void setDesign(DesignFactory design) {
		this.design = design;
	}

	/**
	 * Getter for element.
	 * 
	 * @return The element.
	 */
	public String getElement() {
		return element;
	}

	/**
	 * Setter for element
	 * 
	 * @param element The element.
	 */
	public void setElement(String element) {
		this.element = element;
	}
	
	/**
	 * Setter for property definitions.
	 *  
	 * @param index The index.
	 * @param property The property definition.
	 */
	public void setProperties(int index, PropertyDefinitionBean property) {
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
	 * @return The property definitions.
	 */
	public Collection<PropertyDefinitionBean> toPropertyDefinitions() {
		return properties;
	}
	
	/**
	 * Setter for annotations.
	 * 
	 * @param index The index
	 * @param annotation The Annotation Definition.
	 */
	public void setAnnotations(int index, AnnotationDefinitionBean annotation) {
		if (annotation == null) {
			annotations.remove(index);
		}
		else {
			annotations.add(index, annotation);
		}
	}

	/**
	 * Convert annotations to a collection.
	 *
	 * @return The Annotation Definitions.
	 */
	public Collection<AnnotationDefinitionBean> toAnnotationDefinitions() {
		return annotations;
	}
	
	
	boolean isArooaBeanDescriptor() {
		
		if (parsingInterceptor != null) {
			return true;
		}

		return !properties.isEmpty();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ", element=" + element;
	}
}

