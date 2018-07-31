package org.oddjob.arooa.design.view.multitype;

import javax.swing.table.TableModel;

/**
 * The strategy used to layout a {@link MultiTypeTableWidget}.
 *  
 * @author rob
 *
 */
public interface MultiTypeStrategy {

	enum Strategies implements MultiTypeStrategy {
		
		/**
		 * When the widget is for an indexed property.
		 */
		LIST {
			@Override
			public TableModel tableModelFor(MultiTypeModel multiTypeModel) {
				return new ListMultiTypeAdaptor(multiTypeModel);
			}
			@Override
			public int getTypeColumn() {
				return 0;
			}

			@Override
			public int getNameColumn() {
				return -1;
			}

			@Override
			public int getValueColumn() {
				return 1;
			}
		},
		/**
		 * When the widget is for a mapped property.
		 */
		KEYED {
			@Override
			public TableModel tableModelFor(MultiTypeModel multiTypeModel) {
				return new KeyedMultiTypeAdaptor(multiTypeModel);
			}
			@Override
			public int getTypeColumn() {
				return 0;
			}

			@Override
			public int getNameColumn() {
				return 1;
			}

			@Override
			public int getValueColumn() {
				return 2;
			}
		},
		/**
		 * When the widget is for Oddjob's variables designer.
		 */
		NAMED {
			@Override
			public TableModel tableModelFor(MultiTypeModel multiTypeModel) {
				return new NamedMultiTypeAdaptor(multiTypeModel);
			}
			@Override
			public int getTypeColumn() {
				return 1;
			}

			@Override
			public int getNameColumn() {
				return 0;
			}

			@Override
			public int getValueColumn() {
				return 2;
			}
		}
		;
	}
	
	/**
	 * Create the Swing table model for the table in the widget.
	 * 
	 * @param multiTypeModel The model.
	 * @return A Swing TableModel.
	 */
	public TableModel tableModelFor(MultiTypeModel multiTypeModel);
	
	/**
	 * Get the index of the type column. Will change for the Oddjob
	 * variables layout.
	 * 
	 * @return The index.
	 */
	public int getTypeColumn();

	/**
	 * Get the index of the name column. Will be -1 if no name, i.e. for
	 * lists.
	 * 
	 * @return The index.
	 */
	public int getNameColumn();

	/**
	 * Get the index of the value column.
	 * 
	 * @return The index.
	 */
	public int getValueColumn();

}
