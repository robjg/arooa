package org.oddjob.arooa;

/**
 * Enum representing how a property is configured.
 * 
 * @author rob
 *
 */
public enum ConfiguredHow {

	/**
	 * As an XML attribute.
	 */
	ATTRIBUTE,
	
	/**
	 * An XML element.
	 */
	ELEMENT,
	
	/**
	 * As XML text.
	 */
	TEXT,
	
	/**
	 * The property can not be set from the configuration.
	 */
	HIDDEN,
}
