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
public class TabGroup implements FormItem {

	private String heading;
	
	private final List<FormItem> elements = 
		new ArrayList<FormItem>();

	public TabGroup() {
	}
	
	public TabGroup(String heading) {
		this.heading = heading;
	}

	public FormItem setTitle(String title) {
		this.heading = title;
		return this;
	}
	
	public String getTitle() {
		return this.heading;
	}
	
	public FormItem get(int index) {
		return elements.get(index);
	}
		
	public int size() {
		return elements.size();
	}	
	
	public TabGroup add(FormItem elementField) {
		if (elementField == null) {
			throw new NullPointerException("Element of Group.");
		}
		elements.add(elementField);
		return this;
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
