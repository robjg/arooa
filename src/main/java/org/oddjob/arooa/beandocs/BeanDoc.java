package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.beandocs.element.BeanDocElement;

import java.util.List;

/**
 * Documentation for a bean.
 */
public interface BeanDoc {

	/**
	 * Provide the name of the element which is of the form prefix:tag if there is a namespace prefix or just
	 * the tag if not. Examples would be 'echo' and 'mail:send'.
	 *
	 * @return The element name.
	 */
	String getName();

	/**
	 * The namespace prefix if any.
	 *
	 * @return The prefix, may be null.
	 */
	String getPrefix();

	/**
	 * The element tag name without any prefix.
	 *
	 * @return The tag name, never null.
	 */
	String getTag();

	/**
	 * The fully qualified name of the implementation class.
	 *
	 * @return The class name, never null.
	 */
	String getClassName();

	/**
	 * The first sentence of the description.
	 *
	 * @return List of Bean Doc Elements. Maybe be empty but never null.
	 */
	List<BeanDocElement> getFirstSentence();

	/**
	 * All the description including the first sentence.
	 *
	 * @return List of Bean Doc Elements. Maybe be empty but never null.
	 */
	List<BeanDocElement> getAllText();

	/**
	 * All the property docs for the bean.
	 *
	 * @return An Array of Property Doc. Maybe be empty but never null.
	 */
	PropertyDoc[] getPropertyDocs();

	/**
	 * The property doc for the given property.
	 *
	 * @return The Property Doc. Maybe null.
	 */
	PropertyDoc propertyDocFor(String property);

	/**
	 * All the example docs for the bean.
	 *
	 * @return An Array of Example Doc. Maybe be empty but never null.
	 */
	ExampleDoc[] getExampleDocs();
	
}
