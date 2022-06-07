package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.design.DesignFactory;

import java.util.Collection;

/**
 * Provide a definition of a bean.
 *
 * @see BeanDefinitionBean
 * 
 * @author rob
 *
 */
public interface BeanDefinition {


	/**
	 * Getter the parsing for interceptor.
	 * 
	 * @return A Parsing Interceptor or null if not set.
	 */
	ParsingInterceptor getInterceptor();

	/**
	 * Getter for class name;
	 * 
	 * @return
	 */
	String getClassName();


	/**
	 * Getter for design factory.
	 *
	 * @return A Design Factory class name or null if not provided.
	 */
	String getDesignFactory();

	/**
	 * Getter for Design. Either this or a class name can be provided.
	 *
	 * @return A Design Factory or null.
	 */
	DesignFactory getDesign();

	/**
	 * Get the element name.
	 * 
	 * @return The element name for this bean. Must not be null.
	 */
	String getElement();


	/**
	 * Convert the property definitions to a collection.
	 * 
	 * @return A Collection of Property Definitions. Might be empty, will not be null.
	 */
	Collection<PropertyDefinitionBean> toPropertyDefinitions();
	

	/**
	 * Convert annotations to a collection.
	 *
	 * @return A collection of Annotation Definitions. Might be empty, will not be null.
	 */
	Collection<AnnotationDefinitionBean> toAnnotationDefinitions();

}

