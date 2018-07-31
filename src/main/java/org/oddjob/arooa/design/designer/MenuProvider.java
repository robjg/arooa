package org.oddjob.arooa.design.designer;

import javax.swing.JMenu;

/**
 * Provides menus for a Swing view.
 * 
 * @author rob
 *
 */
public interface MenuProvider extends PopupMenuProvider {

	/**
	 * Get the menus that are the menu bar.
	 * 
	 * @return
	 */
	public JMenu[] getJMenuBar();
	
}
