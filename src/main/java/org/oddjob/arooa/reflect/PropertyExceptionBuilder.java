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
	
	/**
	 * Add a class to the exception message.
	 * 
	 * @param theClass
	 * 
	 * @return This.
	 */
	public PropertyExceptionBuilder forClass(Class<?> theClass) {
		this.theClass = theClass;
		return this;
	}
	
	/**
	 * Add a bean to the exception message.
	 * 
	 * @param bean
	 * 
	 * @return This.
	 */
	public PropertyExceptionBuilder forBean(Object bean) {
		this.bean = bean;
		return this;
	}
	
	/**
	 * Provide an {@link BeanOverview} of the bean causing the problem.
	 * 
	 * @param overview
	 * 
	 * @return This.
	 */
	public PropertyExceptionBuilder withOverview(BeanOverview overview) {
		this.overview = overview;
		return this;
	}
	
	/**
	 * Add the exception that is the cause.
	 * 
	 * @param cause
	 * 
	 * @return This.
	 */
	public PropertyExceptionBuilder causedBy(Exception cause) {
		this.cause = cause;
		return this;
	}
	
	/**
	 * Provide an {@link ArooaNoPropertyException} for when failing
	 * to read a property.
	 * 
	 * @param property
	 * 
	 * @return The exception.
	 */
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
		
	/**
	 * Provide an {@link ArooaNoPropertyException} for when failing
	 * to write a property.
	 * 
	 * @param property
	 * 
	 * @return The exception.
	 */
	public ArooaNoPropertyException failedWritingPropertyException(
			String property) {
	
		String[] writableProperties = null;
		if (overview != null) {
			List<String> writables = new ArrayList<String>();
			String[] properties = overview.getProperties();
			for (String p: properties) {
				if (overview.hasWriteableProperty(p)) {
					writables.add(p);
				}
			}
			writableProperties = writables.toArray(new String[writables.size()]);
		}
		
		String message = "Failed writing property [" + property + "] " 
				+ getBeanAndClassMessageSection();
		
		if (writableProperties != null) {
			message += ", all writeable properties: " + 
				Arrays.toString(writableProperties);
		}
		
		return new ArooaNoPropertyException(property, message, cause);

	}
		
	/**
	 * Provide an {@link ArooaNoPropertyException} for when there is no
	 * property either readable or writable.
	 * 
	 * @param property
	 * 
	 * @return
	 */
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
	
	/**
	 * Helper method to get the  bean and class part of the message.
	 * 
	 * @return
	 */
	protected String getBeanAndClassMessageSection() {
		
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
