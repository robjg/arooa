/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design;

import org.oddjob.arooa.design.screem.FormItem;
import org.oddjob.arooa.design.screem.TextField;

/**
 * A DesignElement that is an attribute in an objects
 * configuration.
 * <p>
 * 
 * @author Rob Gordon.
 */
public class SimpleTextAttribute  
implements DesignAttributeProperty {

	/** The attribute */
	private String attribute;

	private final String property;
	
	/**
	 * Constructor.
	 * 
	 * @param property The property name.
	 * @param owner The owning design. The element will be used to 
	 * 		retrieve the initial attribute value.
	 */
	public SimpleTextAttribute(String property, DesignInstance owner) {
		this.property = property;
		this.attribute = owner.element().getAttributes().get(property);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.design.DesignProperty#property()
	 */
	public String property() {
		return property;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.oddjob.designer.model.DesignAttribute#attribute()
	 */
	public String attribute() {
		return attribute;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.oddjob.designer.model.DesignAttribute#attribute(java.lang.String)
	 */
	public void attribute(String value) {
		if ("".equals(value)) {
			attribute = null;
		}
		else {
			attribute = value;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.design.DesignProperty#view()
	 */
	public FormItem view() {
		return new TextField(this);
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.design.DesignProperty#isPopulated()
	 */
	public boolean isPopulated() {
		return attribute != null && attribute.length() > 0;
	}
}
