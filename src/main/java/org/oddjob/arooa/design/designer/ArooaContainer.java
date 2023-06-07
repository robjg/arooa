package org.oddjob.arooa.design.designer;

import org.oddjob.arooa.parsing.DragPoint;

import javax.swing.TransferHandler.TransferSupport;

/**
 * Something that supports Drag and Drop from Swing.
 */
public interface ArooaContainer {

	DragPoint getCurrentDragPoint();

	DropPoint dropPointFrom(TransferSupport support);
	
	interface DropPoint {
		
		int getIndex();
		
		DragPoint getDragPoint();
	}
	
}
