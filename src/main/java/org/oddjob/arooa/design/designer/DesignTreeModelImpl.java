package org.oddjob.arooa.design.designer;

import org.oddjob.arooa.design.DesignComponent;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * The Swing Tree Model of a Design.
 * <p>
 * The model allows the root component to be changed.
 * 
 * @see DesignTreeNode
 * 
 * @author Rob Gordon 
 */
public class DesignTreeModelImpl implements DesignTreeModel {

	/** Tree model listeners. */
	private final List<TreeModelListener> tmListeners =
			new ArrayList<>();
	
	/** The root node. */
	private DesignTreeNode root;
		
	/**
	 * Set the root component.
	 * 
	 * @param node The root component.
	 */
	public void setRoot(DesignComponent node) {
		if (root != null) {
			root.destroy();
		}
	
		root = new DesignTreeNode(this, null, node);
	
		fireTreeStructureChanged(root);
		
		root.build();
	}
	
	public void addTreeModelListener(TreeModelListener tml) {
		synchronized (tmListeners) {
			tmListeners.add(tml);
		}
	}

	
	public DesignTreeNode getChild(Object parent, int index) {
		return ((DesignTreeNode)parent).getChildAt(index);
	}

	public boolean isLeaf(Object node) {
		return ((DesignTreeNode)node).isLeaf();		
	}

	public int getChildCount(Object parent) {
		return ((DesignTreeNode)parent).getChildCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		return ((DesignTreeNode)parent).getIndex((DesignTreeNode)child);	
	}
	
	public DesignTreeNode getRoot() {
		return root;
	}

	public void removeTreeModelListener(TreeModelListener tml) {
		tmListeners.remove(tml);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		System.err.println("valueForPathChanged");
	}

	private TreeNode[] pathTo(TreeNode node) {
		LinkedList<TreeNode> list = new LinkedList<>();
		for (TreeNode i = node; i != null; i = i.getParent()) {	
			list.addFirst(i);		
		}
		return list.toArray(new TreeNode[0]);		
	}
	
	void fireTreeStructureChanged(TreeNode changed) {
		
		TreeModelEvent event = new TreeModelEvent(
				changed, pathTo(changed));		

		synchronized (tmListeners) {
			for (TreeModelListener tml : tmListeners) {	
				tml.treeStructureChanged(event);
			}		
		}
	}

	void fireTreeNodesInserted(TreeNode changed, DesignTreeNode child, int index) {

		int[] childIndices = {index};
		Object[] children = {child};

		TreeModelEvent event = new TreeModelEvent(
				changed, pathTo(changed), 
				childIndices, children);

		synchronized (tmListeners) {
			for (TreeModelListener tml : tmListeners) {
				tml.treeNodesInserted(event);
			}		
		}
	}

	void fireTreeNodesRemoved(TreeNode changed, DesignTreeNode child, int index) {
		LinkedList<TreeNode> list = new LinkedList<>();
		
		for (TreeNode i = changed; i != null; i = i.getParent()) {
			list.addFirst(i);		
		}

		int[] childIndices = {index};
		Object[] children = {child};

		TreeModelEvent event = new TreeModelEvent(changed, list.toArray(), 
				childIndices, children);

		synchronized (tmListeners) {
			for (TreeModelListener tml : tmListeners) {	
				tml.treeNodesRemoved(event);
			}		
		}
	}
}
