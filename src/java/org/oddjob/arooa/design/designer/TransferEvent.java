package org.oddjob.arooa.design.designer;

import java.util.EventObject;

/**
 * Event broadcast from an {@link ArooaTransferHandler}.
 * 
 * @author rob
 *
 */
public class TransferEvent extends EventObject {
	private static final long serialVersionUID = 2009090400L;
	
	public TransferEvent(ArooaTransferHandler transferHandler) {
		super(transferHandler);
	}
	
	@Override
	public ArooaTransferHandler getSource() {
		return (ArooaTransferHandler) super.getSource();
	}
}
