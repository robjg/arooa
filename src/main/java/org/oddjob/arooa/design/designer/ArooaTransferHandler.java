package org.oddjob.arooa.design.designer;

import org.oddjob.arooa.parsing.DragPoint;
import org.oddjob.arooa.parsing.DragTransaction;
import org.oddjob.arooa.registry.ChangeHow;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

/**
 * A TransferHandler for an {@link ArooaTree}.
 * 
 * @author rob
 *
 */
public class ArooaTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 2009011200L;

	private final List<TransferEventListener> listeners =
			new ArrayList<>();
	
	@Override
	public int getSourceActions(JComponent c) {
		ArooaTree arooaTree = (ArooaTree) c;
		
		TreePath path = arooaTree.getSelectionPath();
		
		if (path == null) {
			return NONE;
		}
		
		Object treeNode = path.getLastPathComponent();
		
		DragPoint dragPoint = null;
		try {
			dragPoint =  arooaTree.getDragPoint(treeNode);
		} catch (Exception e) {
			fireFailureEvent("Failed getting drag point.", e);
		}
			
		if (dragPoint == null) {
			return NONE;
		}
		
		if (!dragPoint.supportsCut()) {
			return COPY;
		}
		
		return COPY_OR_MOVE;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		ArooaContainer arooaContainer = (ArooaContainer) c;
				
		DragPoint dragPoint = arooaContainer.getCurrentDragPoint();

		dragPoint.beginChange(ChangeHow.FRESH);
		
		return new StringSelection(dragPoint.copy());
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {

		ArooaContainer arooaContainer = (ArooaContainer) source;
		
		DragPoint dragPoint = arooaContainer.getCurrentDragPoint();

		if (dragPoint == null) {
			return;
		}
		
		if (action == NONE) {
			DragTransaction transaction = dragPoint.beginChange(ChangeHow.MAYBE);
			if (transaction != null) {
				// failed drag
				transaction.rollback();
			}
			return;
		}

		DragTransaction transaction = dragPoint.beginChange(ChangeHow.AGAIN);

		if (action == MOVE) {

				dragPoint.delete();
		}
		
		try {
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			fireFailureEvent("Failed removing " + data, e);
		}
	}
	
	@Override
	public boolean canImport(TransferSupport support) {
		
		if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return false;
		}
		
		JTree.DropLocation dropLocation = 
			(JTree.DropLocation) support.getDropLocation();
		
		TreePath path = dropLocation.getPath();
		if (path == null) {
			return false;
			
		}
		
		Object treeNode = path.getLastPathComponent();
		
		ArooaTree arooaTree = (ArooaTree) support.getComponent();
				
		DragPoint dragPoint =  arooaTree.getDragPoint(treeNode);
				
		if (dragPoint == null) {
			return false;
		}
		
		return dragPoint.supportsPaste();
	}
	
	@Override
	public boolean importData(TransferSupport support) {
		
		if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return false;
		}
		
		Transferable transferable = support.getTransferable();
		
		String data;
		try {
			data = (String) transferable.getTransferData(
					DataFlavor.stringFlavor);
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		ArooaContainer arooaContainer = (ArooaContainer) support.getComponent();
		
		DragPoint dragPoint;		
		int index;
		
		if (support.isDrop()) {
			ArooaContainer.DropPoint dropPoint = arooaContainer.dropPointFrom(support);
			dragPoint = dropPoint.getDragPoint();
			index = dropPoint.getIndex();
		}
		else {
			dragPoint =  arooaContainer.getCurrentDragPoint();
			
			if (dragPoint == null || !dragPoint.supportsPaste()) {
				return false;
			}
			index = -1;
		}			
			
		DragTransaction transaction = dragPoint.beginChange(ChangeHow.EITHER);
		try {
			dragPoint.paste(index, data);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			fireFailureEvent("Failed " +
					(index < 0 ? "adding " : "inserting ") + data, e);
			return false;
		}
		
		return true;
	}
	
	protected void fireFailureEvent(String reason, Exception exception) {
		List<TransferEventListener> copy;
		synchronized (listeners) {
			if (listeners.isEmpty()) {
				return;
			}
			copy = new ArrayList<>(listeners);
		}
		TransferEvent event = new TransferEvent(this);
		for (TransferEventListener listener: copy) {
			listener.transferException(event, reason, exception);
		}
	}
	
	/**
	 * Add a Listener to receive transfer events.
	 * 
	 * @param listener
	 */
	public void addTransferEventListener(TransferEventListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	/**
	 * Remove a listener.
	 * 
	 * @param listener
	 */
	public void removeTransferEventListener(TransferEventListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
}
