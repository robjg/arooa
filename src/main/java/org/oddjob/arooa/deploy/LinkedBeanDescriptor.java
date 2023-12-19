package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ParsingInterceptor;

/**
 * Link together two {@link ArooaBeanDescriptor}s.
 * 
 * @author rob
 *
 */
public class LinkedBeanDescriptor implements ArooaBeanDescriptor {

	private final ArooaBeanDescriptor primary;
	
	private final ArooaBeanDescriptor secondary;
	
	public LinkedBeanDescriptor(ArooaBeanDescriptor primary,
			ArooaBeanDescriptor secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}
	
	@Override
	public String getComponentProperty() {
		String result = primary.getComponentProperty();
		if (result != null) {
			return result;
		}
		return secondary.getComponentProperty();
	}
	
	@Override
	public ConfiguredHow getConfiguredHow(String property) {
		ConfiguredHow result = primary.getConfiguredHow(property);
		if (result != null) {
			return result;
		}
		return secondary.getConfiguredHow(property);
	}
	
	@Override
	public String getFlavour(String property) {
		String result = primary.getFlavour(property);
		if (result != null) {
			return result;
		}
		return secondary.getFlavour(property);
	}
	
	@Override
	public ParsingInterceptor getParsingInterceptor() {
		ParsingInterceptor result = primary.getParsingInterceptor();
		if (result != null) {
			return result;
		}
		return secondary.getParsingInterceptor();
	}
	
	@Override
	public String getTextProperty() {
		String result = primary.getTextProperty();
		if (result != null) {
			return result;
		}
		return secondary.getTextProperty();
	}
	
	@Override
	public boolean isAuto(String property) {
		if (primary.isAuto(property)) {
			return true;
		}
		return secondary.isAuto(property);
	}
	
	@Override
	public ArooaAnnotations getAnnotations() {
		ArooaAnnotations annotations = primary.getAnnotations();
		if (annotations == null) {
			return secondary.getAnnotations();
		}
		else {
			return annotations;
		}
	}
	
}
