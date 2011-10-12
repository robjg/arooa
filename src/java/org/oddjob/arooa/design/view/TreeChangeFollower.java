package org.oddjob.arooa.design.view;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Utility class that tracks the selected tree node and moves it when
 * the node is deleted.
 * 
 * @author rob
 *
 */
public class TreeChangeFollower {

	private final JTree tree;
		
	private TreeNode lastSelected;
	
	private TreePath lastRemovedParentPath;
	
	private int lastRemovedIndex = -1;
	
	private final TreeModelListener modelListener = new TreeModelListener() {
		
		@Override
		public void treeStructureChanged(TreeModelEvent e) {
		}
		
		@Override
		public void treeNodesRemoved(TreeModelEvent e) {
			TreeNode removed = (TreeNode) e.getChildren()[0];
			
			if (removed == lastSelected) {
				lastRemovedParentPath = e.getTreePath();
				lastRemovedIndex = e.getChildIndices()[0];
				
				if (lastRemovedIndex == 0) {
					tree.setSelectionPath(lastRemovedParentPath);
				}
				else {
					TreeNode parentNode = (TreeNode) lastRemovedParentPath.getLastPathComponent();
					TreeNode previousSibling = parentNode.getChildAt(lastRemovedIndex-1);
					TreePath newPath = lastRemovedParentPath.pathByAddingChild(previousSibling);
					tree.setSelectionPath(newPath);
				}
			}
			else { 
				lastRemovedIndex = -1;
				lastRemovedParentPath = null;
			}
		}
		
		@Override
		public void treeNodesInserted(TreeModelEvent e) {
			if (lastRemovedParentPath != null && 
					lastRemovedParentPath.equals(e.getTreePath()) && 
					e.getChildIndices()[0] == lastRemovedIndex) {
				tree.setSelectionPath(e.getTreePath().pathByAddingChild(
						e.getChildren()[0]));
				lastRemovedParentPath = null;
				lastRemovedIndex = -1;
			}
		}
		
		@Override
		public void treeNodesChanged(TreeModelEvent e) {
		}
	};
	
	private final TreeSelectionListener selectionListner = new TreeSelectionListener() {
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			lastSelected = (TreeNode) e.getPath().getLastPathComponent();
		}
	};
	
	public TreeChangeFollower(JTree tree) {
		this.tree = tree;
		
		TreePath selectionPath = tree.getSelectionPath();
		if (selectionPath != null) {
			this.lastSelected = 
				(TreeNode) selectionPath.getLastPathComponent();
		}
		
		tree.getModel().addTreeModelListener(modelListener);
		tree.addTreeSelectionListener(selectionListner);
	}
	
	public void close() {	
		tree.removeTreeSelectionListener(selectionListner);
		tree.getModel().removeTreeModelListener(modelListener);
	}
}
