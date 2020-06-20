/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.screem;

import org.oddjob.arooa.design.DesignAttributeProperty;
import org.oddjob.arooa.design.SimpleTextAttribute;


/**
 * A model for a visual component is a file name that can be populated via a
 * file selection.
 */
public class FileSelection 
implements FormItem, Form {

	private String heading;
	private final DesignAttributeProperty attribute;
	
	public FileSelection(SimpleTextAttribute de) {
		this.heading = de.property();
		this.attribute = de;
	}
	
	public FileSelection(String heading, SimpleTextAttribute de) {
		this.heading = heading;
		this.attribute = de;
	}

	public FormItem setTitle(String title) {
		this.heading = title;
		return this;
	}
	
	public String getTitle() {
		return heading;
	}
	
	/**
	 * Used by the view to set text value of the field.
	 * 
	 * @param file The file name text.
	 */
	public void setFile(String file) {
		if (file == null) {
			attribute.attribute(null);
		}
		else {
			attribute.attribute(file);
		}
	}

	public DesignAttributeProperty getAttribute() {
		return attribute;
	}

	/**
	 * Used by the view to get the text value for the field.
	 * 
	 * @return The file name text.
	 */
	public String getFile() {
		return attribute.attribute();
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.designer.model.DesignDefinition#isPopulated()
	 */
	public boolean isPopulated() {
		return attribute.attribute() != null;
	}
}
