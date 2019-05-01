package org.oddjob.arooa.design.designer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.oddjob.arooa.design.DesignComponent;


/**
 * The Swing Tree Model of a Design.
 * <p>
 * The model allows the root component to be changed.
 * 
 * @see DesignTreeNode
 * 
 * @author Rob Gordon 
 */
public interface DesignTreeModel extends TreeModel {

	@Override
	DesignTreeNode getRoot();
}
