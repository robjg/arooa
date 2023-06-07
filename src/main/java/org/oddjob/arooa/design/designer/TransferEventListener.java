package org.oddjob.arooa.design.designer;

import java.util.EventListener;

/**
 * Listener for events from an {@link ArooaTransferHandler}. 
 * Currently the only notification is failure. Other events could be
 * added if required. 
 * 
 * @author rob
 *
 */
public interface TransferEventListener extends EventListener {

	/**
	 * Transfer failed.
	 * 
	 * @param event The event. 
	 * @param message The reason.
	 * @param exception The underlying exception.
	 */
	void transferException(TransferEvent event, String message, Exception exception);
}
