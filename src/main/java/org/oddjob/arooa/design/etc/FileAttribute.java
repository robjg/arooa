/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.etc;

import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.SimpleTextAttribute;
import org.oddjob.arooa.design.screem.FileSelection;
import org.oddjob.arooa.design.screem.FormItem;

/**
 * For an attribute that describes a file.
 * 
 */
public class FileAttribute extends SimpleTextAttribute {
	
	public FileAttribute(String property, DesignInstance owner) {
		super(property, owner);
	}
	
	public FormItem view() {
		return new FileSelection(this);
	}
	

}
