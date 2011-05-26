package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;

/**
 * @oddjob.description Provide a definition for a property within
 * an {@link BeanDefinition}. 
 * <p>
 * Providing property definitions within a BeanDefinition is an alternative
 * to using annotations in the Java code or providing an 
 * {@link ArooaBeanDescriptor} Arooa class file.
 * 
 * @author rob
 *
 */
public class PropertyDefinition {

	public enum PropertyType {
		ATTRIBUTE,
		ELEMENT,
		TEXT,
		COMPONENT,
		HIDDEN
	}
	
	/** 
     * @oddjob.property
     * @oddjob.description The name of the property.
     * @oddjob.required Yes.
	 */
	private String name;
	
	/** 
     * @oddjob.property
     * @oddjob.description The type of the property. One
     * of ATTRIBUTE, ELEMENT, TEXT, COMPONENT, HIDDEN.
     * @oddjob.required Yes.
	 */
	private PropertyType type;

	/** 
     * @oddjob.property
     * @oddjob.description Not used at present.
     * @oddjob.required No.
	 */
	private String flavour;
	
	/** 
     * @oddjob.property
     * @oddjob.description Is the property set automatically by the 
     * framework. True/False.
     * @oddjob.required No. Defaults to false.
	 */
	private boolean auto;
	
	/**
	 * No Arg Constructor.
	 */
	public PropertyDefinition() {}
	
	/**
	 * Constructor when used from code.
	 * 
	 * @param name
	 * @param type
	 */
	public PropertyDefinition(
			String name, PropertyType type) {
		this.name = name;
		this.type = type;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	boolean isComponentProperty() {
		return type == PropertyType.COMPONENT;
	}

	boolean isTextProperty() {
		return type == PropertyType.TEXT;
	}
	
	public void setType(PropertyType type) {
		this.type = type;
	}

	public PropertyType getType() {
		return type;
	}
	
	ConfiguredHow getConfiguredHow() {
		switch (type) {
		case ATTRIBUTE:
			return ConfiguredHow.ATTRIBUTE;
		case ELEMENT:
			return ConfiguredHow.ELEMENT;
		case TEXT:
			return ConfiguredHow.TEXT;
		case COMPONENT:
			return ConfiguredHow.ELEMENT;
		case HIDDEN:
			return ConfiguredHow.HIDDEN;
		}
		return null;
	}

	@Override
	public String toString() {
		return "PropertyDefinitionBean: name=" + name + 
			", type=" + type + ".";
	}

	public String getFlavour() {
		return flavour;
	}

	public void setFlavour(String flavour) {
		this.flavour = flavour;
	}

	public boolean isAuto() {
		return auto;
	}

	public void setAuto(boolean auto) {
		this.auto = auto;
	}
}
