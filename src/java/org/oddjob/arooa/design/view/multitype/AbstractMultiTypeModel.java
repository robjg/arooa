package org.oddjob.arooa.design.view.multitype;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared model implementation.
 * 
 * @author rob
 *
 */
abstract public class AbstractMultiTypeModel implements MultiTypeModel {

	private final List<MultiTypeListener> listeners = 
			new ArrayList<MultiTypeListener>();

	@Override
	public void addMultiTypeListener(MultiTypeListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public void removeMultiTypeListener(MultiTypeListener listener) {
		this.listeners.remove(listener);
	}
	
	protected void fireRowChanged(int rowIndex) {
		MultiTypeEvent event = new MultiTypeEvent(this, rowIndex);
		for (MultiTypeListener listener : listeners) {
			listener.rowChanged(event);
		}
	}
	
	protected void fireRowInserted(int rowIndex) {
		MultiTypeEvent event = new MultiTypeEvent(this, rowIndex);
		for (MultiTypeListener listener : listeners) {
			listener.rowInserted(event);
		}
	}
	
	protected void fireRowRemoved(int rowIndex) {
		MultiTypeEvent event = new MultiTypeEvent(this, rowIndex);
		for (MultiTypeListener listener : listeners) {
			listener.rowRemoved(event);
		}
	}	
}
