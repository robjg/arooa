package org.oddjob.arooa.design;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.screem.FormItem;
import org.oddjob.arooa.design.screem.SingleTypeSelection;

/**
 * A {@link DesignElementProperty} for a simple property, i.e. not an 
 * indexed property or a mapped property.
 * 
 * @see IndexedDesignProperty
 * @see MappedDesignProperty
 * 
 * @author rob
 *
 */
public class SimpleDesignProperty extends DesignPropertyBase {

	/** The design of the value of the property */
	private DesignInstance instance;

	/**
	 * Constructor for {@link GenericDesignFactory}.
	 * 
	 * @param property
	 * @param propertyClass
	 * @param type
	 * @param parent
	 */
	public SimpleDesignProperty(String property, 
			Class<?> propertyClass,
			ArooaType type, DesignInstance parent) {
		super(property, propertyClass, type, parent);		
	}
	
	/**
	 * Constructor for typical instances.
	 * 
	 * @param property The property name.
	 * @param parent The parent design instance.
	 */
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
	
	public FormItem view() {
		return new SingleTypeSelection(this);
	}

	public boolean isPopulated() {
		return instance != null;
	}	
}
