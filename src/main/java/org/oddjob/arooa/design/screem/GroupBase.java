/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.screem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * Base class for DesignDefinitions that group other DesignDefinitions.
 * 
 * @author Rob Gordon.
 */
public class GroupBase implements FormItem {

	private String heading;
	
	private final List<FormItem> elements = 
		new ArrayList<FormItem>();

	public GroupBase() {
	}
	
	public GroupBase(String heading) {
		this.heading = heading;
	}

	public FormItem setTitle(String title) {
		this.heading = title;
		return this;
	}
	
	public String getTitle() {
		return this.heading;
	}
	
	protected void addElement(FormItem elementField) {
		if (elementField == null) {
			throw new NullPointerException("Element of Group.");
		}
		elements.add(elementField);
	}
			
	public FormItem get(int index) {
		return elements.get(index);
	}
		
	public int size() {
		return elements.size();
	}	
	
	public boolean isPopulated() {
		boolean populated = false;
		for (Iterator<FormItem> it = elements.iterator(); it.hasNext(); ) {
			if (it.next().isPopulated()) {
				populated = true;
			}
		}
		return populated;
	}
}
