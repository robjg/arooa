package org.oddjob.arooa.design;


/**
 * This is an internal extension of a {@link DesignInstance} that
 * provides information for parsing.
 * 
 * @see DesignConfiguration
 * 
 * @author rob
 *
 */
public interface ParsableDesignInstance extends DesignInstance {

	/**
	 * Subclasses implement this to provide the child properties.
	 * 
	 * @return Child properties. Must not be null.
	 */
	DesignProperty[] children();

}
