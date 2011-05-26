package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ParsingInterceptor;

public class BeanDescriptorHelper implements ArooaBeanDescriptor {

	private final ArooaBeanDescriptor beanDescriptor;
	
	public BeanDescriptorHelper(ArooaBeanDescriptor beanDescriptor) {
		this.beanDescriptor = beanDescriptor;
	}
	
	public ParsingInterceptor getParsingInterceptor() {
		if (beanDescriptor == null) {
			return null;
		}
		return beanDescriptor.getParsingInterceptor();
	}
	
	public ConfiguredHow getConfiguredHow(String property) {
		ConfiguredHow how = null;
		if (beanDescriptor != null) {
			how = beanDescriptor.getConfiguredHow(property);
		}
		if (how == null) {
			return ConfiguredHow.ELEMENT;
		}
		return how;
	}
	
	public String getTextProperty() {
		if (beanDescriptor == null) {
			return null;
		}
		return beanDescriptor.getTextProperty();
	}
	
	/**
	 * The name of the component property.
	 * 
	 * @return
	 */
	public String getComponentProperty() {
		if (beanDescriptor == null) {
			return null;
		}
		return beanDescriptor.getComponentProperty();
	}

	public boolean isAttribute(String property) {
		if (beanDescriptor == null) {
			return false;
		}
		
		return beanDescriptor.getConfiguredHow(property) == ConfiguredHow.ATTRIBUTE;
	}
	
	public boolean isElement(String property) {
		return getConfiguredHow(property) == ConfiguredHow.ELEMENT;
	}
	
	public boolean isComponent(String property) {
		if (beanDescriptor == null) {
			return false;
		}
		
		return property.equals(beanDescriptor.getComponentProperty());
	}
	
	public ArooaType getArooaType(String property) {
		if (beanDescriptor != null
				&& property.equals(beanDescriptor.getComponentProperty())) {
			return ArooaType.COMPONENT;
		}
		
		return ArooaType.VALUE;
	}
	
	public boolean isText(String property) {
		return getConfiguredHow(property) == ConfiguredHow.TEXT;
	}
	
	public boolean isHidden(String property) {
		if (beanDescriptor == null) {
			return false;
		}
		
		return beanDescriptor.getConfiguredHow(property) == ConfiguredHow.HIDDEN;
	}
	
	public boolean isAuto(String property) {
		if (beanDescriptor == null) {
			return false;
		}
		
		return beanDescriptor.isAuto(property);
	}
	
	public String getFlavour(String property) {
		if (beanDescriptor == null) {
			return null;
		}
		
		return beanDescriptor.getFlavour(property);
	}
}
