/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPopupMenu;

import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.OkAware;

/**
 * Utility methods for Swing Views.
 */
public class ViewHelper {

	/**
	 * Create a standard detail button for a DesignDefinition.
	 * 
	 * @param def The DesignDefinition.
	 * 
	 * @return A button.
	 */
	public static Component createDetailButton(final Form def) {
		final JButton button = new JButton();
		button.setAction(new AbstractAction("Edit") {
			private static final long serialVersionUID = 2008101500L;
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				Component form = SwingFormFactory.create(def).dialog();

				ValueDialog valueDialog;
				if ( def instanceof OkAware) {
					valueDialog = new ValueDialog(form, ((OkAware) def).getOkAction());
				}
				else {
					valueDialog = new ValueDialog(form);
				}
				valueDialog.showDialog(button, true);
			}
		});
		return button;
	}
	
	/**
	 * Pad the label of a field.
	 * 
	 * @param title The text.
	 * @return Padded text.
	 */
	public static String padLabel(String title) {
		StringBuilder paddedTitle = new StringBuilder();
		paddedTitle.append(title);
	      	for (int i = title.length(); i < Looks.LABEL_SIZE; ++i) {
			paddedTitle.append(' ');
		}
		return paddedTitle.toString();
	}
	
	/**
	 * Utility method to get the parent window for a component. Required for
	 * dialogs.
	 *  
	 * @param parentComponent The component who's window were finding.
	 * 
	 * @return The window.
	 * @throws HeadlessException If the component has no window.
	 */
	public static Window getWindowForComponent(Component parentComponent)
	throws HeadlessException {
		if (parentComponent == null)
			return null;
		if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
			return (Window)parentComponent;
		if (parentComponent instanceof JPopupMenu) {
			// a popup doesn't have a parent!!
			return ViewHelper.getWindowForComponent(
					((JPopupMenu) parentComponent).getInvoker());
		}
		return ViewHelper.getWindowForComponent(parentComponent.getParent());
	}
}