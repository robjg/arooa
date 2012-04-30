package org.oddjob.arooa.design.view.multitype;

/**
 * The table model for a list of values.
 * 
 * @author rob
 *
 */
public class ListMultiTypeAdaptor extends TypeMultiTypeAdaptor {
	private static final long serialVersionUID = 2012042600;

	private final String[] HEADERS = { "Type", "Value" };
	
	/**
	 * Constructor.
	 * 
	 * @param model The model.
	 */
	public ListMultiTypeAdaptor(MultiTypeModel model) {
		super(model);
	}
	
	public String getColumnName(int column) {
		return HEADERS[column];
	}

	public int getColumnCount() {
		return 2;
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
			return row.getValue();
		}
		throw new RuntimeException("This should be impossible!");
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {

		if (columnIndex == 0) {
			handleType(value, rowIndex, columnIndex);
		}
	}
}
