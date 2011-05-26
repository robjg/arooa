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
     * @oddjob.description a list of property definitions.
     * @oddjob.required No.
	 */
	private final List<PropertyDefinition> properties = 
		new ArrayList<PropertyDefinition>();
	
	public void setInterceptor(ParsingInterceptor parsingInterceptor) {
		this.parsingInterceptor = parsingInterceptor;
	}
	
	public ParsingInterceptor getInterceptor() {
		return parsingInterceptor;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDesignFactory() {
		return designFactory;
	}

	public void setDesignFactory(String designFactory) {
		this.designFactory = designFactory;
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}
	
	public void setProperties(int index, PropertyDefinition property) {
		if (property == null) {
			properties.remove(index);
		}
		else {	
			properties.add(index, property);
		}
	}

	public Collection<PropertyDefinition> toPropertyDefinitions() {
		return properties;
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
	
}

