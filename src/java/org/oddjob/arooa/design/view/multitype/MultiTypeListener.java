package org.oddjob.arooa.design.view.multitype;

import java.util.EventListener;

/**
 * Listener for changes in a {@link MultiTypeModel}
 * 
 * @author rob
 *
 */
public interface MultiTypeListener extends EventListener {

	/**
	 * Called when a row has changed.
	 * 
	 * @param event The event
	 */
	public void rowChanged(MultiTypeEvent event);
	
	/**
	 * Called when a row has been inserted.
	 *  
	 * @param event The event.
	 */
	public void rowInserted(MultiTypeEvent event);

	/**
	 * Called when a row has been removed.
	 * 
	 * @param event The event.
	 */
	public void rowRemoved(MultiTypeEvent event);
}
