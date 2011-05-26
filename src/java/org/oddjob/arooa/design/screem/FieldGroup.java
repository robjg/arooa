/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.screem;

/**
 * 
 */
public class FieldGroup extends GroupBase  {

	private boolean containsSelection;

	public FieldGroup() {
	}
	
	public FieldGroup(String heading) {
		super(heading);
	}
	
	public FieldGroup add(FormItem elementField) {
		super.addElement(elementField);
		if (elementField instanceof FieldSelection) {
			containsSelection = true;			
		}
		return this;
	}
			
	public boolean isContainsSelection() {
		return containsSelection;
	}
	
}
