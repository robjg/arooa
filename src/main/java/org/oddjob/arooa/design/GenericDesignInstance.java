package org.oddjob.arooa.design;

/**
 * A {@link DesignInstance} that can be used to design any object. As
 * opposed to most DesignInstances that know about their children, a
 * GenericDesignInstance is told about it's children.
 * 
 * @author rob
 *
 */
interface GenericDesignInstance extends DesignInstance {

	/**
	 * Accept the properties for the instance being represented.
	 * 
	 * @param children The properties. Must never be null.
	 */
	public void children(DesignProperty[] children);
	
}
