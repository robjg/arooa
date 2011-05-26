package org.oddjob.arooa.design.designer;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/**
 * Provides menus for a Swing view.
 * 
 * @author rob
 *
 */
public interface MenuProvider {

	/**
	 * Get the menus that are the menu bar.
	 * 
	 * @return
	 */
	public JMenu[] getJMenuBar();
	
	/**
	 * Get the pop-up menu.
	 * 
	 * @return
	 */
	public JPopupMenu getPopupMenu();
	
}
