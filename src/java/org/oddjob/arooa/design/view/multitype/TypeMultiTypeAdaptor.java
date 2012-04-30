package org.oddjob.arooa.design.view.multitype;

/**
 * Shared implementation for the two models where the element drives
 * creation of a row.
 * 
 * @author rob
 *
 */
abstract public class TypeMultiTypeAdaptor extends AbstractMultiTypeAdaptor {
	private static final long serialVersionUID = 2012042600;

	/**
	 * Constructor.
	 * 
	 * @param model The model.
	 */
	public TypeMultiTypeAdaptor(MultiTypeModel model) {
		super(model);
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return true;
		}
		if (rowIndex < model.getRowCount()) {
			return true;
		}
		return false;
	}

	/**
	 * Create/Delete a new row.
	 * 
	 * @param value
	 * @param rowIndex
	 * @param columnIndex
	 */
	protected void handleType(Object value, int rowIndex, int columnIndex) {

		if (rowIndex < model.getRowCount()) {
			MultiTypeRow row = model.getRow(rowIndex);
			Object creator = row.getType();
			if (creator.equals(value)) {
				// if the type hasn't changed then nothing to do.
				return;
			} else {
				model.removeRow(rowIndex);
			}
		}

		// Nothing to insert
		if (value == null) {
			return;				
		}
		if (value == model.getDeleteOption()) {
			return;
		}

		// otherwise insert.
		model.createRow(value, rowIndex);
	}
}
