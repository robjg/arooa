package org.oddjob.arooa.design.view.multitype;

/**
 * The model used to provide data to a {@link MultiTypeTableWidget} and
 * process changes from the view.
 * 
 * @author rob
 *
 */
public interface MultiTypeModel {
	
	/**
	 * Add a listener that will receive notification of changes to this
	 * model.
	 * 
	 * @param listener The listener.
	 */
	public void addMultiTypeListener(MultiTypeListener listener);
	
	/**
	 * Remove a listener.
	 * 
	 * @param listener The listener.
	 */
	public void removeMultiTypeListener(MultiTypeListener listener);
	
	/**
	 * Get the options that are the type. These will be the elements
	 * supported by a property.
	 * 
	 * @return The options. Never null.
	 */
	public Object[] getTypeOptions();

	/**
	 * Get the option that causes a row to be deleted. This must be 
	 * the same instance each time because the widget uses == to compere
	 * the value to this.
	 * 
	 * @return The delete object.
	 */
	public Object getDeleteOption();
	
	/**
	 * The number of rows.
	 * 
	 * @return The number of rows.
	 */
	public int getRowCount();
	
	/**
	 * Create a row.
	 * 
	 * @param creator The thing that drives the creation. Either a new
	 * element type or the name of a variable.
	 * @param row The row being created.
	 */
	public void createRow(Object creator, int row);
	
	/**
	 * Get a row.
	 * 
	 * @param index The index of the row.
	 * @return A row.
	 */
	public MultiTypeRow getRow(int index);
	
	/**
	 * Remove a row.
	 * 
	 * @param index The index of the row.
	 */
	public void removeRow(int index);

	/**
	 * Swap two rows.
	 * 
	 * @param from The row from.
	 * @param direction The direction of the swap, +1 or -1.
	 */
	public void swapRow(int from, int direction);	
}
