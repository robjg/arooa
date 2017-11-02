package org.oddjob.arooa.design.designer;
import org.junit.Before;

import org.junit.Test;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.junit.Assert;

import org.oddjob.arooa.design.ClipboardHelper;
import org.oddjob.arooa.parsing.DragPoint;
import org.oddjob.arooa.parsing.DragTransaction;
import org.oddjob.arooa.parsing.MockDragPoint;
import org.oddjob.arooa.registry.ChangeHow;

public class ArooaTransferHandlerTest extends Assert {

   @Before
   public void setUp() throws Exception {

		if (GraphicsEnvironment.isHeadless()) {
			return;
		}
		
		new ClipboardHelper().setText("Previous Clipboard Contents");
	}
	
	class OurDragPoint extends MockDragPoint {
	
		DefaultMutableTreeNode treeNode;
		
		OurDragPoint(DefaultMutableTreeNode treeNode) {
			this.treeNode = treeNode;
		}
		
		@Override
		public boolean supportsCut() {
			return false;
		}
		
		@Override
		public DragTransaction beginChange(ChangeHow how) {
			return new DragTransaction() {
				public void commit() {
				}
				public void rollback() {
				}
			};
		}
		
		@Override
		public String copy() {
			return (String) treeNode.getUserObject();
		}
	}
	
	class OurTree extends ArooaTree {
		private static final long serialVersionUID = 2009020400L;
		
		OurTree(TreeNode root) {
			super(root);
		}
		
		@Override
		protected DragPoint getDragPoint(Object treeNode) {
			assertNotNull(treeNode);
			return new OurDragPoint((DefaultMutableTreeNode) treeNode);
		}
	}
	
	
   @Test
	public void testCopy() {
	
		if (GraphicsEnvironment.isHeadless()) {
			return;
		}
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("fruit");

		root.add(new DefaultMutableTreeNode("apple"));

		OurTree ourTree	= new OurTree(root);
		ourTree.setTransferHandler(new ArooaTransferHandler());

		ourTree.setSelectionRow(1);
		
		Action cutAction = ourTree.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME));
		
		cutAction.actionPerformed(
				new ActionEvent(ourTree, 0, (String) cutAction.getValue(Action.NAME)));

		String result = new ClipboardHelper().getText();
		
		assertEquals("apple", result);
	}
	
	class NoDragPointTree extends ArooaTree {
		private static final long serialVersionUID = 2009020400L;
		
		NoDragPointTree(TreeNode root) {

		}
		
		@Override
		public DragPoint getDragPoint(Object treeNode) {
			return null;
		}
	}

	
   @Test
	public void testNoDragPoint() {
		
		if (GraphicsEnvironment.isHeadless()) {
			return;
		}
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("fruit");

		root.add(new DefaultMutableTreeNode("apple"));

		NoDragPointTree ourTree = new NoDragPointTree(root);
		ourTree.setTransferHandler(new ArooaTransferHandler());

		ourTree.setSelectionRow(1);
		
		Action cutAction = ourTree.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME));
		
		cutAction.actionPerformed(
				new ActionEvent(ourTree, 0, (String) cutAction.getValue(Action.NAME)));

		String result = new ClipboardHelper().getText();
				
		assertEquals("Previous Clipboard Contents", result);
	}
}
