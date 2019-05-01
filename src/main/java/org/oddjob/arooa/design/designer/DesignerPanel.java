package org.oddjob.arooa.design.designer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.DropMode;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.oddjob.arooa.design.view.TreePopup;
import org.oddjob.arooa.parsing.DragContext;
import org.oddjob.arooa.parsing.DragPoint;

/**
 * The tree view.
 * 
 * @author Rob Gordon
 */

public class DesignerPanel extends JPanel {
	private static final long serialVersionUID = 2008100100;

	private final JTree tree;

	private final JScrollPane treeScroll;

	/**
	 * Constructor.
	 * 
	 * @param model The model.
	 * @param menuBar The menu provider.
	 */
	
	public DesignerPanel(final DesignerModel model, MenuProvider menuBar) {
		
		// create tree.
		tree = new ArooaTree(model.getTreeModel()) {
			private static final long serialVersionUID = 20090115L;
			
			@Override
			public DragPoint getDragPoint(Object treeNode) {
				DesignTreeNode designTreeNode = 
					(DesignTreeNode) treeNode; 
				return new DragContext(
						designTreeNode.getDesignComponent().getArooaContext()) {
				};
			}
		};
		
		tree.setShowsRootHandles(true);
		tree.setExpandsSelectedPaths(true);
		
		// drag and drop
		tree.setDragEnabled(true);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		
		ArooaTransferHandler transferHandler = new ArooaTransferHandler();
		transferHandler.addTransferEventListener(new TransferEventListener() {			
			public void transferException(TransferEvent event, String message, Exception exception) {
				String text = message + "\n" + "Cause: " + exception.getMessage();
				JOptionPane.showMessageDialog(
						tree, 
						text,
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
		
		tree.setTransferHandler(transferHandler);
		
		tree.getSelectionModel().setSelectionMode
			(TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent event) {
				// change selection in the model
				JTree tree = (JTree)event.getSource();
				DesignTreeNode node = (DesignTreeNode)tree.getLastSelectedPathComponent();
				model.setCurrentSelection(node);				
			}
		});
		tree.getModel().addTreeModelListener(new TreeModelListener() {
			public void treeNodesChanged(TreeModelEvent e) {
			}
			public void treeNodesInserted(TreeModelEvent e) {
				tree.scrollPathToVisible(e.getTreePath());
			}
			public void treeNodesRemoved(TreeModelEvent e) {
			}
			public void treeStructureChanged(TreeModelEvent e) {
			}
		});
		
		new TreePopup(tree, menuBar);

		tree.addAncestorListener(new AncestorListener() {
			
			@Override
			public void ancestorRemoved(AncestorEvent event) {
				tree.removeAncestorListener(this);
			}
			
			@Override
			public void ancestorMoved(AncestorEvent event) {
			}
			
			@Override
			public void ancestorAdded(AncestorEvent event) {
				tree.requestFocusInWindow();
			}
		});
		
		setLayout(new BorderLayout());
		treeScroll = new JScrollPane();		
		treeScroll.setViewportView(tree);
		
		// create detail pane
		DesignerDetail designerDetail = new DesignerDetail();

		model.addObserver(designerDetail);

		// This must be after the detail observer so the detail panel 
		// is sized correctly to start.
		tree.setSelectionPath(new TreePath(tree.getModel().getRoot()));
		
		// attempt to the tree scroll. Not that setMiniumSize was
		// being used but this was ignored by the split pane
		// when calculator size but cause the split to be pushed across
		// scrunching up the detail.
		// try and proportion so tree is at least a third of the width
		int minimum = (int) designerDetail.getPreferredSize().getWidth() / 2;
		if (treeScroll.getPreferredSize().getWidth() < minimum) {
			treeScroll.setPreferredSize(new Dimension(
					minimum, 
					(int) treeScroll.getPreferredSize().getHeight()));
		}
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			treeScroll, designerDetail);
		
		add(split);
		
	}
		
}
