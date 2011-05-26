/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.screem;

import org.oddjob.arooa.design.DesignAttributeProperty;


/**
 * Groups a attribute and it's title.
 */
public class TextField implements FormItem {

	private String title;
	private final DesignAttributeProperty attribute;
	
	public TextField(DesignAttributeProperty attribute) {
		this(attribute.property(), attribute);
	}
	
	public TextField(String title, DesignAttributeProperty attribute) {
		if (title == null) {
			throw new NullPointerException("Null title not allowed!");
		}
		if (attribute == null) {
			throw new NullPointerException("Null DesignElement not allowed!");
		}
		this.title = title;		
		this.attribute = attribute;		
	}

	public FormItem setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public String getTitle() {
		return title;
	}
	
	public DesignAttributeProperty getAttribute() {
		return attribute;
	}	

	/**
	 * Does this contain any data of any sort. Used in selection dialogs to
	 * see if a group containing this element should be selected.
	 */
	public boolean isPopulated() {
		return attribute.isPopulated();
	}
	
}
