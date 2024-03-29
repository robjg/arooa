/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa;

/**
 * A description of any special types of properties a 
 * class might have.
 * <p>
 * @see ArooaDescriptor
 *
 */
public interface ArooaBeanDescriptor {

	/**
	 * Get The {@link ParsingInterceptor}.
	 * 
	 * @return A Parsing Interceptor or null if one isn't available
	 */
	ParsingInterceptor getParsingInterceptor();
	
	/**
	 * The name of the property which can be set using the text
	 * of an xml element. At most only one property can be set
	 * using the text of an element.
	 * <p>
	 * The actual property name is required because unlike 
	 * components and values, the property name can not be
	 * derrived from an element name.
	 * 
	 * @return The name of the property to be set using the
	 * text of the element. Will be null if the class doesn't
	 * support setting element text.
	 */
	String getTextProperty();
	
	/**
	 * The name of the component property. A bean can have at most one
	 * component property.
	 * 
	 * @return The name of the property or null if there isn't one.
	 */
	String getComponentProperty();

	/**
	 * Get the type of the property.
	 *
	 * @param property The property.
	 * @return The type. Never null.
	 */
	default ArooaType getArooaType(String property) {

		return property.equals(getComponentProperty()) ?
			ArooaType.COMPONENT : ArooaType.VALUE;
	}

	/**
	 * How is a property configured.
	 * 
	 * @param property The property name.
	 * 
	 * @return How the property is configured. Never null.
	 */
	ConfiguredHow getConfiguredHow(String property);
	
	/**
	 * Get the property flavour. This is for services.
	 * 
	 * @param property The property name.
	 * 
	 * @return The flavour. May be null.
	 */
	String getFlavour(String property);
	
	/**
	 * Indicates that the property should be set automatically
	 * from the services.
	 * 
	 * @param property The property name.
	 * 
	 * @return true if the property can be set automatically,
	 * false otherwise.
	 */
	boolean isAuto(String property);
		
	/**
	 * Provide annotation information about methods. Used to allow
	 * life cycle methods, and any other custom annotation methods
	 * to defined separately from the class.
	 * 
	 * @return The annotations.
	 */
	ArooaAnnotations getAnnotations();
}
