package org.oddjob.arooa.reflect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provide a more flexible way of building a {@link ArooaPropertyException}.
 * 
 * @author rob
 *
 */
public class PropertyExceptionBuilder {

	private Class<?> theClass;
	
	private Object bean;
	
	private BeanOverview overview;
	
	private Exception cause;
	
	public PropertyExceptionBuilder forClass(Class<?> theClass) {
		this.theClass = theClass;
		return this;
	}
	
	public PropertyExceptionBuilder forBean(Object bean) {
		this.bean = bean;
		return this;
	}
	
	public PropertyExceptionBuilder withOverview(BeanOverview overview) {
		this.overview = overview;
		return this;
	}
	
	public PropertyExceptionBuilder causedBy(Exception cause) {
		this.cause = cause;
		return this;
	}
	
	public ArooaNoPropertyException failedReadingPropertyException(
			String property) {
	
		String[] readableProperties = null;
		if (overview != null) {
			List<String> readables = new ArrayList<String>();
			String[] properties = overview.getProperties();
			for (String p: properties) {
				if (overview.hasReadableProperty(p)) {
					readables.add(p);
				}
			}
			readableProperties = readables.toArray(new String[readables.size()]);
		}
		
		String message = "Failed reading property [" + property + "] " 
				+ getBeanAndClassMessageSection();
		
		if (readableProperties != null) {
			message += ", all readable properties: " + 
				Arrays.toString(readableProperties);
		}
		
		return new ArooaNoPropertyException(property, message, cause);

	}
		
	public ArooaNoPropertyException noPropertyException(
			String property) {
		
		String[] properties = null;
		if (overview != null) {
			properties = overview.getProperties();
		}
		
		String message = "There is no property [" + property + "] " 
				+ getBeanAndClassMessageSection();
		
		if (properties != null) {
			message += ", properties: " + 
				Arrays.toString(properties);
		}
		
		return new ArooaNoPropertyException(property, message, cause);
	}
	
	public String getBeanAndClassMessageSection() {
		
		if (bean != null && theClass == null) {
			theClass = bean.getClass();
		}
		
		String message = "";
		
		if (theClass == null) {
			return message;
		}
		
		if (bean != null) {
			message += " on object [" + bean + "]";
		}
		
		message += " (" + theClass.getName() + ")";
		
		return message;
	}
}
