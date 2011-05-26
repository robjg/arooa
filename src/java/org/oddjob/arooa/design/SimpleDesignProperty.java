package org.oddjob.arooa.design;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.screem.FormItem;
import org.oddjob.arooa.design.screem.SingleTypeSelection;

public class SimpleDesignProperty extends DesignPropertyBase {

	private DesignInstance instance;

	public SimpleDesignProperty(String property, 
			Class<?> propertyClass,
			ArooaType type, DesignInstance parent) {
		super(property, propertyClass, type, parent);		
	}
	
	public SimpleDesignProperty(String property, DesignInstance parent) {
		super(property, parent);		
	}
	
	void insertInstance(int index, DesignInstance instance) {
		if (instance != null && this.instance != null) {
			throw new IllegalArgumentException("Only one child allowed.");
		}
		this.instance = instance;
	}
	
	int instanceCount() {
		return instance == null ? 0 : 1;
	}
	
	DesignInstance instanceAt(int index) {
		return instance;
	}
	
	public void clear() {
		instance = null;
	}

	public FormItem view() {
		return new SingleTypeSelection(this);
	}

	public boolean isPopulated() {
		return instance != null;
	}	
}
