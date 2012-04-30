package org.oddjob.arooa.design.view.multitype;

import javax.swing.table.AbstractTableModel;

/**
 * Shared implementation for MultiType adaptors.
 * 
 * @author rob
 */
abstract public class AbstractMultiTypeAdaptor 
extends AbstractTableModel {
	private static final long serialVersionUID = 2012042600;

	protected final MultiTypeModel model;
	
	/**
	 * Constructor.
	 * 
	 * @param model The model.
	 */
	public AbstractMultiTypeAdaptor(MultiTypeModel model) {
		this.model = model;
		model.addMultiTypeListener(new MultiTypeListener() {
			
			@Override
			public void rowChanged(MultiTypeEvent event) {
				fireTableRowsUpdated(
						event.getRow(), event.getRow());
			}
			
			@Override
			public void rowRemoved(MultiTypeEvent event) {
				fireTableRowsDeleted(
						event.getRow(), event.getRow());
			}
			
			@Override
			public void rowInserted(MultiTypeEvent event) {
				fireTableRowsInserted(
						event.getRow(), event.getRow());
			}
		});
	}
	
	public int getRowCount() {
		return model.getRowCount() + 1;
	}
}
