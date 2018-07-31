/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.etc;

import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.SimpleTextAttribute;

/**
 * For an attribute that can only ever be a ${} reference.
 * 
 * Currently just extends SimpleDE but one day could support
 * property browsing... 
 */
public class ReferenceAttribute extends SimpleTextAttribute {

	public ReferenceAttribute(String property, DesignInstance owner) {
		super(property, owner);
	}
	
}
