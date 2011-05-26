/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.screem;



/**
 * 
 */
abstract public class SelectionList implements FormItem {

	private final String heading;
	
	public SelectionList(String heading) {
		this.heading = heading;		
	}

	public String getTitle() {
		return heading;
	}

	abstract public String[] getOptions();
	
	abstract public void setSelected(String selected);

	abstract public String getSelected();
	
}
