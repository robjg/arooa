package org.oddjob.arooa.design.view.multitype;


/**
 * A TableModel for a MultiType where name is first. Used for Oddjob's
 * Variables designer.
 * 
 * @author rob
 *
 */
public class NamedMultiTypeAdaptor extends AbstractMultiTypeAdaptor {
	private static final long serialVersionUID = 2012042600;

	private final String[] HEADERS = { "Name", "Type", "Value" };
	
	/**
	 * Constructor.
	 * 
	 * @param model The model.
	 */
	public NamedMultiTypeAdaptor(MultiTypeModel model) {
		super(model);
	}
	
	public String getColumnName(int column) {
		return HEADERS[column];
	}

	public int getColumnCount() {
		return 3;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return true;
		}
		if (rowIndex < model.getRowCount()) {
			if (columnIndex == 2) {
				return !(getValueAt(rowIndex, columnIndex) == null);
			}
			return true;
		}
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex == model.getRowCount()) {
			return null;
		}

		MultiTypeRow row = model.getRow(rowIndex);

		switch (columnIndex) {
		case 0:
			return row.getName();
		case 1:
			return row.getType();
		case 2:
			return row.getValue();
		}
		throw new RuntimeException("This should be impossible!");
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {

		if (columnIndex == 0) {

			if (rowIndex < model.getRowCount()) {
				MultiTypeRow row = model.getRow(rowIndex);
				Object name = row.getName();
				if (name.equals(value)) {
					// if the type hasn't changed then nothing to do.
					return;
				}
				if ("".equals(value)) {
					model.removeRow(rowIndex);
				}
				else {
					row.setName((String) value);
				}
			}
			else {
				// Nothing to insert
				if (value == null || "".equals(value)) {
					return;				
				}
				// otherwise insert.
				model.createRow(value, rowIndex);
			}
		} else if (columnIndex == 1) {
			MultiTypeRow row = model.getRow(rowIndex);
			row.setType(value);
		} 
	}		
}
