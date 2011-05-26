package org.oddjob.arooa.design.designer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DropMode;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.oddjob.arooa.design.view.Looks;
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

	private final MenuProvider menuBar;

	/**
	 * Constructor.
	 * 
	 * @param start The starting point for the tree.
	 */
	
	public DesignerPanel(final DesignerModel model, MenuProvider menuBar) {
		// set up the menu
		this.menuBar = menuBar;
		
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
		
		tree.addMouseListener(new PopupListener());

		setLayout(new BorderLayout());
		treeScroll = new JScrollPane();
		treeScroll.setPreferredSize(new Dimension(
				Looks.DESIGNER_TREE_WIDTH, Looks.DESIGNER_HEIGHT));
		
		treeScroll.setViewportView(tree);

		// create detail pane
		DesignerDetail dd = new DesignerDetail();
		dd.setPreferredSize(new Dimension(
				Looks.DETAIL_FORM_WIDTH, Looks.DESIGNER_HEIGHT));
		model.addObserver(dd);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			treeScroll, dd);

		add(split);
		
	}
		
	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}
		public void mouseClicked(MouseEvent e) {
			maybeShowPopup(e);
		}
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
		
		private void maybeShowPopup(MouseEvent e) {
			if (!e.isPopupTrigger()) {
				return;
			}
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			tree.setSelectionPath(path);
			if (menuBar.getPopupMenu() != null) {
				menuBar.getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

}
