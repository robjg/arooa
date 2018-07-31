package org.oddjob.arooa.design.designer;

import javax.swing.JPopupMenu;

/**
 * Something that can provide a Popup Menu.
 * 
 * @author rob
 *
 */
public interface PopupMenuProvider {

	/**
	 * Get the pop-up menu.
	 * 
	 * @return
	 */
	public JPopupMenu getPopupMenu();
	
}
