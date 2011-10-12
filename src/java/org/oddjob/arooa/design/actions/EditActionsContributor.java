package org.oddjob.arooa.design.actions;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

import org.oddjob.arooa.design.designer.ArooaTree;

/**
 * Provides the cut/copy/paste and delete actions.
 * 
 * @author rob
 *
 */
public class EditActionsContributor implements ActionContributor {

	public static final String EDIT_MENU_ID = "edit-menu";
	
	public static final String EDIT_GROUP = "edit";
	
	private final ArooaAction cutAction;
	
	private final ArooaAction copyAction;
	
	private final ArooaAction pasteAction;
		
	private final ArooaAction deleteAction;
	
	public EditActionsContributor() {
		final TransferActionListener actionListener = new TransferActionListener();

		cutAction = new AbstractArooaAction() {
			private static final long serialVersionUID = 2008121700L;
			
			{
				putValue(NAME, "Cut");
				putValue(MNEMONIC_KEY, KeyEvent.VK_T); 
				putValue(ACCELERATOR_KEY, 
						KeyStroke.getKeyStroke(KeyEvent.VK_X,
								ActionEvent.CTRL_MASK));
				putValue(ACTION_COMMAND_KEY, 
						TransferHandler.getCutAction().getValue(NAME));
			}
			
			public void actionPerformed(ActionEvent e) {
				actionListener.actionPerformed(e);
			}
			
		};
		
		copyAction = new AbstractArooaAction() {
			private static final long serialVersionUID = 2008121700L;
			
			{
				putValue(NAME, "Copy");
				putValue(MNEMONIC_KEY, KeyEvent.VK_C);
				putValue(ACCELERATOR_KEY, 
						KeyStroke.getKeyStroke(KeyEvent.VK_C,
								ActionEvent.CTRL_MASK));
				putValue(ACTION_COMMAND_KEY, 
						TransferHandler.getCopyAction().getValue(NAME));
			}
			
			public void actionPerformed(ActionEvent e) {
				actionListener.actionPerformed(e);
			}
			
		};
		
		pasteAction = new AbstractArooaAction() {
			private static final long serialVersionUID = 2008121700L;
			
			{
				putValue(NAME, "Paste");
				putValue(MNEMONIC_KEY, KeyEvent.VK_P); 
				putValue(ACCELERATOR_KEY, 
						KeyStroke.getKeyStroke(KeyEvent.VK_V,
								ActionEvent.CTRL_MASK));
				putValue(ACTION_COMMAND_KEY, 
						TransferHandler.getPasteAction().getValue(NAME));
			}
			
			public void actionPerformed(ActionEvent e) {
				actionListener.actionPerformed(e);
			}
			
		};

		deleteAction = new AbstractArooaAction() {
			private static final long serialVersionUID = 2008121700L;
			
			{
				putValue(Action.NAME, "Delete");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D); 
				putValue(ACCELERATOR_KEY, 
						KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
				putValue(ACTION_COMMAND_KEY, ArooaTree.DELETE_COMMAND);

			}
			
			public void actionPerformed(ActionEvent e) {
				actionListener.actionPerformed(e);
			}
			
		};
	}
	
	public void contributeTo(ActionRegistry actionRegistry) {

		actionRegistry.addMainMenu(
				new ActionMenu(
						EDIT_MENU_ID, 
						"Edit",
						KeyEvent.VK_E));
		
		actionRegistry.addMenuItem(EDIT_MENU_ID, EDIT_GROUP, cutAction);
		actionRegistry.addMenuItem(EDIT_MENU_ID, EDIT_GROUP, copyAction);
		actionRegistry.addMenuItem(EDIT_MENU_ID, EDIT_GROUP, pasteAction);
		actionRegistry.addMenuItem(EDIT_MENU_ID, EDIT_GROUP, deleteAction);
		
		actionRegistry.addContextMenuItem(EDIT_GROUP, cutAction);
		actionRegistry.addContextMenuItem(EDIT_GROUP, copyAction);
		actionRegistry.addContextMenuItem(EDIT_GROUP, pasteAction);		
		actionRegistry.addContextMenuItem(EDIT_GROUP, deleteAction);		
	}

	protected void setCutEnabled(boolean enabled) {
		cutAction.setEnabled(enabled);
	}
	
	protected void setCopyEnabled(boolean enabled) {
		copyAction.setEnabled(enabled);
	}
	
	protected void setPasteEnabled(boolean enabled) {
		pasteAction.setEnabled(enabled);
	}
	
	protected void setDeleteEnabled(boolean enabled) {
		deleteAction.setEnabled(enabled);
	}
	

	
	/**
	 * http://java.sun.com/docs/books/tutorial/uiswing/examples/dnd/ListCutPasteProject/src/dnd/TransferActionListener.java 
	 * 
	 * @author rob
	 *
	 */
	private class TransferActionListener implements ActionListener,
			PropertyChangeListener {
		private JComponent focusOwner = null;

		public TransferActionListener() {
			KeyboardFocusManager manager = KeyboardFocusManager
					.getCurrentKeyboardFocusManager();
			manager.addPropertyChangeListener("permanentFocusOwner", this);
		}

		public void propertyChange(PropertyChangeEvent e) {
			Object o = e.getNewValue();
			if (o instanceof JComponent) {
				focusOwner = (JComponent) o;
			} else {
				focusOwner = null;
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (focusOwner == null)
				return;
			String action = (String) e.getActionCommand();
			Action a = focusOwner.getActionMap().get(action);
			if (a != null) {
				a.actionPerformed(new ActionEvent(focusOwner,
						ActionEvent.ACTION_PERFORMED, null));
			}
		}
	}
	
	@Override
	public void addKeyStrokes(JComponent component) {
		
		// Keystrokes are registered already in the TransferHandler class,
		// so nothing to do here.
	}
}
