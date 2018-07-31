package org.oddjob.arooa.design.view.multitype;

/**
 * The table model for keyed values.
 * 
 * @author rob
 *
 */
public class KeyedMultiTypeAdaptor extends TypeMultiTypeAdaptor {
	private static final long serialVersionUID = 2012042600;

	private final String[] HEADERS = { "Type", "Key", "Value" };
	
	/**
	 * Constructor.
	 * 
	 * @param model The model.
	 */
	public KeyedMultiTypeAdaptor(MultiTypeModel model) {
		super(model);
	}
	
	public String getColumnName(int column) {
		return HEADERS[column];
	}

	public int getColumnCount() {
		return 3;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex == model.getRowCount()) {
			return null;
		}

		MultiTypeRow row = model.getRow(rowIndex);

		switch(columnIndex) {
		case 0:
			return row.getType();
		case 1:
			return row.getName();
		case 2:
			return row.getValue();
		}
		throw new RuntimeException("This should be impossible!");
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {

		if (columnIndex == 0) {
			handleType(value, rowIndex, columnIndex);
		} else if (columnIndex == 1) {
			MultiTypeRow row = model.getRow(rowIndex);
			row.setName((String) value);
		}
	}
}
