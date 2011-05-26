/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.screem;

import org.oddjob.arooa.design.DesignAttributeProperty;


/**
 * Groups a attribute and it's title.
 */
public class TextPsudoForm implements Form {

	private String title;
	
	private final DesignAttributeProperty attribute;
	
	public TextPsudoForm(DesignAttributeProperty attribute) {
		this(attribute.property(), attribute);
	}
	
	public TextPsudoForm(String title, DesignAttributeProperty attribute) {
		if (title == null) {
			throw new NullPointerException("Null title not allowed!");
		}
		if (attribute == null) {
			throw new NullPointerException("Null DesignElement not allowed!");
		}
		this.title = title;		
		this.attribute = attribute;		
	}

	
	public String getTitle() {
		return title;
	}
	
	public DesignAttributeProperty getAttribute() {
		return attribute;
	}	

}
