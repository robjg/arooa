/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.screem;

import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.InstanceSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A form definition provides a definition for creating the
 * designer detail panel.
 */
public class StandardForm implements Form {

	private final String title;
	
	private final DesignInstance design;
	
	private final List<FormItem> groups =
			new ArrayList<>();
	
	public StandardForm(DesignInstance design) {
		this(null, design);
	}
	
	public StandardForm(String name, DesignInstance design) {
		this.title = Optional.ofNullable(name)
				.orElseGet(() -> InstanceSupport.tagFor(design).toString());
		this.design = design;
	}
	
	public String getTitle() {
		return title;
	}

	public DesignInstance getDesign() {
		return design;
	}
		
	/**
	 * Add a dialog definition which will be rendered as a group on the form.
	 * 
	 * @param designDef A design definition.
	 * @return This form definition.
	 */
	public StandardForm addFormItem(FormItem designDef) {
		groups.add(designDef);
		return this;
	}
	
	public int size() {
		return groups.size();
	}
		
	public FormItem getFormItem(int index) {
		return groups.get(index);
	}
	
	/**
	 * Used by certain summary views to indicate that there is more
	 * detail.
	 * 
	 * @return true if the model has some data, false if it doesn't.
	 */
	public boolean isPopulated() {
		for (FormItem designDefinition: groups) {
			if (designDefinition.isPopulated()) {
				return true;
			}
		}	
		return false;
	}

}
