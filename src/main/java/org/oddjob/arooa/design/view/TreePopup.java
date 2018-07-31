package org.oddjob.arooa.design.view;

import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.oddjob.arooa.design.designer.PopupMenuProvider;

/**
 * Utility class to handle Popup menus.
 * 
 * @author rob
 *
 */
public class TreePopup {

	private final JTree tree;
	
	private final PopupMenuProvider menuBar;
	
	public TreePopup(JTree tree, PopupMenuProvider menuBar) {		
		this.tree = tree;
		this.menuBar = menuBar;
		
		if (menuBar == null) {
			throw new NullPointerException("No menu bar.");
		}
		
		tree.addMouseListener(new PopupListener());
		tree.addKeyListener(new PopupKeyListener());

	}
	
	private class PopupKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() != KeyEvent.VK_CONTEXT_MENU) {
				return;
			}
			TreePath path = tree.getSelectionPath();
			if (path == null) {
				return;
			}
			Rectangle rectangle = tree.getPathBounds(path);
			doPopup((int) rectangle.getX(), (int) rectangle.getY());
		}
	}
	
	/**
	 * Listen to mouse events to trigger the popup.
	 *
	 */
	private class PopupListener extends MouseAdapter {
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
			doPopup(e.getX(), e.getY());
		}
	}
	
	private void doPopup(int x, int y) {
		TreePath path = tree.getPathForLocation(x, y);
		tree.setSelectionPath(path);
		JPopupMenu menu = menuBar.getPopupMenu();
		if (menu != null) {
			menu.show(tree, x, y);
		}
	}
}
