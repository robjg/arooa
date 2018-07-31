/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.screem;

import org.oddjob.arooa.design.SimpleTextAttribute;


/**
 * A model for a visual component is a file name that can be populated via a
 * file selection.
 */
public class FileSelection 
implements FormItem, Form {

	private String heading;
	private final SimpleTextAttribute designElement;
	
	public FileSelection(SimpleTextAttribute de) {
		this.heading = de.property();
		this.designElement = de;
	}
	
	public FileSelection(String heading, SimpleTextAttribute de) {
		this.heading = heading;
		this.designElement = de;
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
	 * @param text The file name text.
	 */
	public void setFile(String file) {
		if (file == null) {
			designElement.attribute(null);
		}
		else {
			designElement.attribute(file);
		}
	}

	/**
	 * Used by the view to get the text value for the field.
	 * 
	 * @return The file name text.
	 */
	public String getFile() {
		return designElement.attribute();
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.designer.model.DesignDefinition#isPopulated()
	 */
	public boolean isPopulated() {
		return designElement.attribute() != null;
	}
}
