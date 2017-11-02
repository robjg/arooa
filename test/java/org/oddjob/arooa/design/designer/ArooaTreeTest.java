package org.oddjob.arooa.design.designer;

import org.junit.Test;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.DropMode;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.junit.Assert;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.parsing.DragContext;
import org.oddjob.arooa.parsing.DragPoint;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;


public class ArooaTreeTest extends Assert {

	interface ChildListener {
		void childAdded(int index, Folder folder);
		void childRemoved(int index);
	}

	static int count;
	
	public static class Folder {

		private ChildListener listener;

		private String name;
		private int instance;
		
		List<Folder> children = new ArrayList<Folder>();
					
		Folder(String name) {
			instance = count++;
			this.name = name;
		}

		public Folder() {
			instance = count++;
			name= "Created";
		}
		
		@ArooaComponent
		public void setChild(int index, Folder child) {
			if (child == null) {
				children.remove(index);
				listener.childRemoved(index);
			}
			else {
				children.add(index, child);
				listener.childAdded(index, child);
			}
		}
		
		@Override
		public String toString() {
			return "Folder " + instance + ": " + name;
		}

	}
	
	JTree test;
	
	XMLConfiguration config = new XMLConfiguration("TEST",
			"<class class='" + Folder.class.getName() + "'/>");	
	
	private class TreeObject {

		final Folder folder;
		
		TreeObject(final DefaultTreeModel model, 
				final DefaultMutableTreeNode owner,
				Folder folder) {
			this.folder = folder;
			folder.listener = new ChildListener() {
				public void childAdded(int arg0, Folder arg1) {
					OurTreeNode newNode = new OurTreeNode();
					TreeObject treeObject = new TreeObject(model, newNode, arg1);
					newNode.setUserObject(treeObject);
					model.insertNodeInto(newNode, owner, arg0);
				}
				public void childRemoved(int arg0) {
					MutableTreeNode child = (MutableTreeNode) owner.getChildAt(arg0);
					model.removeNodeFromParent(child);
				}
			};
		}
		
		@Override
		public String toString() {
			return folder.toString();
		}
	}
	
	private class OurTreeNode extends DefaultMutableTreeNode {
		private static final long serialVersionUID = 2009020400L;
		
		@Override
		public boolean isLeaf() {
			return false;
		}
	}

	private class OurArooaTree extends ArooaTree {
		
		private static final long serialVersionUID = 2009011400L;

		private final ArooaSession session;
		
		OurArooaTree(TreeModel model, ArooaSession session) {
			super(model);
			this.session = session;
		}
		
		@Override
		public DragPoint getDragPoint(Object treeNode) {

			DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treeNode;
			TreeObject obj = (TreeObject) defaultMutableTreeNode.getUserObject();
			Folder folder = obj.folder;

			return new DragContext(session.getComponentPool().contextFor(folder));
		}
	}
	
	
   @Test
	public void testTree() throws ArooaParseException {

		Folder root = new Folder("Root");
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		final ConfigurationHandle handler = parser.parse(config);

		final ArooaSession session = handler.getDocumentContext().getSession();
		
		OurTreeNode rootNode = new OurTreeNode();
		
		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		
		TreeObject treeObj = new TreeObject(model, rootNode, root);
		rootNode.setUserObject(treeObj);
		
		test = new OurArooaTree(model, session);

		assertNotNull(test.getModel());
		assertNotNull(test.isEditable());
		
	}
	
	public static void main(String... args) throws ArooaParseException {

		final ArooaTreeTest test = new ArooaTreeTest();
		test.testTree();
		
		test.test.setDragEnabled(true);
		
		test.test.setDropMode(DropMode.ON_OR_INSERT);
		
		test.test.setTransferHandler(new ArooaTransferHandler());
		
		test.test.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		test.test.setShowsRootHandles(true);
		
		JPanel panel = new JPanel();
		panel.add(test.test);
		
		JFrame frame = new JFrame();
		
		frame.getContentPane().add(panel);
		
		frame.pack();
		frame.setLocation(new Point(800, 400));
		frame.setVisible(true);

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		test.config.setSaveHandler(new XMLConfiguration.SaveHandler() {
			@Override
			public void acceptXML(String xml) {
				System.out.println(xml);
			}
		});		
	}


    @Test
	public void testDelete() throws ArooaParseException {

		Folder root = new Folder("Root");
		
		OurTreeNode rootNode = new OurTreeNode();
		
		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		
		TreeObject treeObj = new TreeObject(model, rootNode, root);
		rootNode.setUserObject(treeObj);
		
		String xml = 
			"<bean class='" + Folder.class.getName() + "'>" +
			" <child>" + 
			"  <bean class='" + Folder.class.getName() + "'/>" +
			" </child>" +
			"</bean>";
		
		XMLConfiguration config = new XMLConfiguration(
				"TEST", xml);
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		final ConfigurationHandle handler = parser.parse(config);

		final ArooaSession session = handler.getDocumentContext().getSession();
		
		test = new OurArooaTree(model, session);
		
		test.setSelectionRow(1);
		
		Action delete = test.getActionMap().get(ArooaTree.DELETE_COMMAND);
		
		delete.actionPerformed(new ActionEvent(test, 1, ArooaTree.DELETE_COMMAND));
		
		assertEquals(0, root.children.size());
	}

}
