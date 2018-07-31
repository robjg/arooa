/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.screem;

import org.oddjob.arooa.design.DesignElementProperty;


/**
 * A model for a view onto a DesignElement which can support
 * one child of different possible types. 
 */
public class SingleTypeSelection implements FormItem {

	private String heading;
	private final DesignElementProperty designProperty;
	
	public SingleTypeSelection(DesignElementProperty designProperty) {
		this(designProperty.property(), designProperty);
	}
	
	public SingleTypeSelection(String heading, DesignElementProperty designProperty) {
		this.heading = heading;
		this.designProperty = designProperty;
		
	}
	
	public FormItem setTitle(String title) {
		this.heading = title;
		return this;
	}
	
	public String getTitle() {
		return heading;
	}

	public DesignElementProperty getDesignElementProperty() {
		return designProperty;
	}
		
	/* (non-Javadoc)
	 * @see org.oddjob.designer.model.DesignDefinition#isPopulated()
	 */
	public boolean isPopulated() {
		return designProperty.isPopulated();
	}
}
