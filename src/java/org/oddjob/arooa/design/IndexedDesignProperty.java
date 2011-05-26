package org.oddjob.arooa.design;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.screem.FormItem;
import org.oddjob.arooa.design.screem.MultiTypeTable;

public class IndexedDesignProperty extends DesignPropertyBase {

	private final List<DesignInstance> instances = 
		new ArrayList<DesignInstance>();	
	
	
	public IndexedDesignProperty(String property,
			Class<?> propertyClass,
			ArooaType type ,
			DesignInstance parent) {
		super(property, propertyClass, type, parent);
	}
	
	public IndexedDesignProperty(String property,
			DesignInstance parent) {
		super(property, parent);
	}
	
	void insertInstance(int index, DesignInstance de) {
		if (de == null) {
			instances.remove(index);
		}
		else {
			if (index == -1) {
				instances.add(de);
			}
			else {
				instances.add(index, de);
			}
		}
	}
	
	public int instanceCount() {
		return instances.size();
	}
	
	public DesignInstance instanceAt(int index) {
		return instances.get(index);
	}
	
	public FormItem view() {
		return new MultiTypeTable(this);
	}
	
	public void clear() {
		instances.clear();
	}

	public boolean isPopulated() {
		return instances.size() > 0;
	}
}
