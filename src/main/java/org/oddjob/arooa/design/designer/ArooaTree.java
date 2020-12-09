package org.oddjob.arooa.design.designer;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.parsing.ConfigurationOwner;
import org.oddjob.arooa.parsing.ConfigurationSession;
import org.oddjob.arooa.parsing.DragPoint;
import org.oddjob.arooa.parsing.DragTransaction;
import org.oddjob.arooa.registry.ChangeHow;

import javax.swing.*;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * A base for Swing JTree sub classes that are able to provide a 
 * {@link DragPoint} for the manipulation of an underlying
 * {@link ArooaConfiguration}.
 * <p>
 * This version of a JTree also supports a 'delete' action
 * in addition to the standard copy, cut, paste.
 *  
 * @author rob
 *
 * @see ConfigurationOwner
 * @see ConfigurationSession
 */
abstract public class ArooaTree extends JTree
implements ArooaContainer {
	
	private static final long serialVersionUID = 2009011400L;
	
	public static final String DELETE_COMMAND = "delete";
	
	public ArooaTree() {
		setUpDeleteAction();
	}
	
	public ArooaTree(TreeNode treeNode, boolean askAllowsChldren) {
		super(treeNode, askAllowsChldren);
		setUpDeleteAction();
	}
	
	public ArooaTree(TreeNode treeNode) {
		super(treeNode);
		setUpDeleteAction();
	}
	
	public ArooaTree(TreeModel model) {
		super(model);
		setUpDeleteAction();
	}

	private void setUpDeleteAction() {
		getActionMap().put(DELETE_COMMAND, new AbstractAction() {
			private static final long serialVersionUID = 2009021900L;
						
			public void actionPerformed(ActionEvent e) {
				TreePath path = getSelectionPath();
				if (path == null) {
					return;
				}
				Object node = path.getLastPathComponent();
				
				DragPoint dragPoint = getDragPoint(node);
				if (dragPoint == null) {
					return;
				}
				DragTransaction trn = dragPoint.beginChange(ChangeHow.FRESH);
				dragPoint.delete();
				try {
					trn.commit();
				} catch (ArooaParseException e1) {
					trn.rollback();
					throw new RuntimeException(
							"Failed to cut - which should be impossible.", e1);
				}
			}
		});
		
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
				DELETE_COMMAND);
	}

	@Override
	public DragPoint getCurrentDragPoint() {
		TreePath path = getSelectionPath();
		
		if (path == null) {
			return null;
		}
		
		Object treeNode = path.getLastPathComponent();
		
		return getDragPoint(treeNode);
	}
	
	@Override
	public DropPoint dropPointFrom(TransferSupport support) {
		JTree.DropLocation dropLocation = 
			(JTree.DropLocation) support.getDropLocation();

		Object treeNode = dropLocation.getPath().getLastPathComponent();
		
		final DragPoint dragPoint =  getDragPoint(treeNode);
	
		final int index = dropLocation.getChildIndex();
		
		return new DropPoint() {
			
			@Override
			public int getIndex() {
				return index;
			}
			
			@Override
			public DragPoint getDragPoint() {
				return dragPoint;
			}
		};
	}
	
	
	/**
	 * Get a {@link DragPoint} for a tree node.
	 * 
	 * @param treeNode The tree node.
	 * 
	 * @return A drag point or null if the node doesn't support dragging.
	 * 
	 */
	abstract protected DragPoint getDragPoint(Object treeNode);
}
