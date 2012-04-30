package org.oddjob.arooa.design.view.multitype;


/**
 * A row in a {@link MultiTypeModel}
 * 
 * @author rob
 */
public interface MultiTypeRow {
	
	/**
	 * Get the type of the row. This will be the element name.
	 * @return
	 */
	public Object getType();

	/**
	 * Set the type. This will be used when the name is driving creation, 
	 * i.e. when being used as for variables.
	 * 
	 * @param type The type of the row.
	 */
	public void setType(Object type);
	
	/**
	 * The name of the row. The will be the key for a mapped type, or the
	 * name of a variable.
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Set name of the row.
	 * 
	 * @param name The name.
	 */
	public void setName(String name);
	
	/**
	 * Get the editor for the value.
	 * 
	 * @return The editor. May be null for an as yet undefined variable.
	 */
	public EditableValue getValue();
}
