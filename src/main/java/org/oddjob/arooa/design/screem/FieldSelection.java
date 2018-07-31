/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.screem;


/**
 * A DesignDefintion that is intended to be rendered as a selection between
 * the child DesignDefinitions.
 * <p>
 * Note that this type can not be nested in itself - this will throw an
 * exception at runtime.
 * 
 *  @author Rob Gordon.
 */
public class FieldSelection extends GroupBase {

	/**
	 * Constructor. A FieldSelection can currently only appear inline so 
	 * the title 'select' is only used for testing.
	 *
	 */
	public FieldSelection() {
		super("select");
	}
	
	/**
	 * Add a child DesignDefinition.
	 * 
	 * @param designDef The child DesignDefinition.
	 * @return This.
	 */
	public FieldSelection add(FormItem designDef) {
		addElement(designDef);
		return this;
	}
		
}
