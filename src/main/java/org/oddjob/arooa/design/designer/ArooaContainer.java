package org.oddjob.arooa.design.designer;

import javax.swing.TransferHandler.TransferSupport;

import org.oddjob.arooa.parsing.DragPoint;

public interface ArooaContainer {

	public DragPoint getCurrentDragPoint();

	public DropPoint dropPointFrom(TransferSupport support);
	
	interface DropPoint {
		
		public int getIndex();
		
		public DragPoint getDragPoint();
	}
	
}
