package org.oddjob.arooa.design.designer;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import org.oddjob.arooa.design.DesignComponent;
import org.oddjob.arooa.design.DesignListener;
import org.oddjob.arooa.design.DesignStructureEvent;
import org.oddjob.arooa.design.view.SwingFormFactory;

/**
 * This class wraps a DesignComponent allowing it to 
 * act as a TreeNode.
 * 
 * @author Rob Gordon 
 */
public class DesignTreeNode 
		implements TreeNode {

	/** For list of children */
	private final Vector<DesignTreeNode> nodeList = 
		new Vector<DesignTreeNode>();

	/** Parent node */
	final private DesignTreeNode parent;

	/** Save the JobTreeModel. */
	final private DesignTreeModel model;

	/** The design component. */
	private final DesignComponent designComponent;

	private Component detailView;
	
	/**
	 * Constructor.
	 * 
	 * @param model The tree model this node belongs to.
	 * @param parent The parent node.
	 * @param node The structure node this is modelling.
	 */
	public DesignTreeNode(DesignTreeModel model, 
			DesignTreeNode parent, DesignComponent node) {
	
		this.model = model;			
		this.parent = parent;
		this.designComponent = node;
	}
	
	/**
	 * Build the tree. This recursive method tracks the component tree 
	 * structure. It creates and remove child tree nodes as the
	 * are added and removed to the component.
	 */
	public void build() {
		designComponent.addStructuralListener(
				new DesignListener() {
					
					public void childAdded(DesignStructureEvent e) {

						int index = e.getIndex();
						Object o = e.getChild();
						if (!(o instanceof DesignComponent)) {
							throw new IllegalStateException("[" + o + "] is not a DesignComponent for child of [" + designComponent + "]" );
						}
						DesignComponent childComponent = (DesignComponent) o;
						
						DesignTreeNode child = new DesignTreeNode(
								model, DesignTreeNode.this, childComponent);

						synchronized (nodeList) {
							nodeList.insertElementAt(child, index);
						}

						model.fireTreeNodesInserted(
								DesignTreeNode.this, child, index);
						child.build();
					}
					
					public void childRemoved(DesignStructureEvent e) {
						
						int index = e.getIndex();
						DesignTreeNode child = null;
						synchronized (nodeList) {
							child = nodeList.elementAt(index);
							nodeList.removeElementAt(index);
						}
						child.destroy();

						model.fireTreeNodesRemoved(
								DesignTreeNode.this, child, index);
					}

				});
	}

	public DesignComponent getDesignComponent() {
		return designComponent;
	}
		
	// TreeNode methods

	public Enumeration<DesignTreeNode> children() {
		return nodeList.elements();
	}

	public boolean getAllowsChildren() {
		return true;
	}
		
	public DesignTreeNode getChildAt(int index) {
		return nodeList.elementAt(index);
	}

	public int getChildCount() {
		return nodeList.size();
	}

	public boolean isLeaf() {
		return nodeList.size() == 0 ? true : false;		
	}

	public int getIndex(TreeNode child) {

		return nodeList.indexOf(child);		
	}

	public DesignTreeNode getParent() {
		
		return parent;
	}

	public String toString() {
		
		return designComponent.toString();
	}
	
	public void destroy() {
		while (nodeList.size() > 0) {			
			int index = nodeList.size() - 1;
			DesignTreeNode child = (DesignTreeNode) nodeList.remove(index);
			child.destroy();
			model.fireTreeNodesRemoved(this, child, index);
		}	
	}
	
	/**
	 * @return Returns the detailView.
	 */
	public Component getDetailView() {
		if (detailView == null) {
			detailView = SwingFormFactory.create(designComponent.detail()).dialog();
		}
		return detailView;
	}
}
