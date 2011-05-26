package org.oddjob.arooa.deploy;

import java.util.LinkedHashMap;
import java.util.Map;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ParsingInterceptor;

/**
 * Help class to provide an {@link ArooaBeanDescriptor}.
 * 
 * @author rob
 *
 */
class PropertyDefinitionsHelper implements ArooaBeanDescriptor {

	private final Map<String, PropertyDefinition> properties = 
		new LinkedHashMap<String, PropertyDefinition>();

	private String componentProperty;
	
	private String textProperty;

	private ParsingInterceptor parsingInterceptor;
	
	public void addPropertyDefinition(PropertyDefinition propertyDefinition) {
		if (propertyDefinition.isComponentProperty()) {
			if (componentProperty != null) {
				throw new IllegalStateException("Component property of " + 
						componentProperty + " can't be changed to " +
						propertyDefinition.getName());
			}
			componentProperty = propertyDefinition.getName();
		}
		if (propertyDefinition.isTextProperty()) {
			if (textProperty != null) {
				throw new IllegalStateException("Text property of " + 
						textProperty + " can't be changed to " +
						propertyDefinition.getName());
			}
			textProperty = propertyDefinition.getName();
		}

		properties.put(propertyDefinition.getName(), propertyDefinition);
	}
	
	public void mergeFromBeanDefinition(BeanDefinition beanDef) {

		for (PropertyDefinition def: beanDef.toPropertyDefinitions()) {
			addPropertyDefinition(def);
		}
		
		parsingInterceptor = beanDef.getInterceptor();
	}
	
	public String getComponentProperty() {
		return componentProperty;
	}
	
	public String getTextProperty() {
		return textProperty;
	}

	public ConfiguredHow getConfiguredHow(String property) {
		PropertyDefinition propertyDefinition = properties.get(property);
		if (propertyDefinition == null) {
			return null;
		}
	
		return propertyDefinition.getConfiguredHow();
	}
	
	public String getFlavour(String property) {
		PropertyDefinition propertyDefinition = properties.get(property);
		if (propertyDefinition == null) {
			return null;
		}
	
		return propertyDefinition.getFlavour();
	}
	
	public boolean isAuto(String property) {
		PropertyDefinition propertyDefinition = properties.get(property);
		if (propertyDefinition == null) {
			return false;
		}
	
		return propertyDefinition.isAuto();
	}

	public ParsingInterceptor getParsingInterceptor() {
		return parsingInterceptor;
	}
	
}
