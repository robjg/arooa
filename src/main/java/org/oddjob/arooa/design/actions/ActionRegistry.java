package org.oddjob.arooa.design.actions;


/**
 * Register Menu Actions. Implementations will build menus from
 * what is registered.
 * <p>
 * The register uses two ideas to position menus. One is menu id which
 * identifies the menu. The other is the group name which fits a menu into
 * a group between menu dividers.
 * 
 * @author rob
 *
 */
public interface ActionRegistry {

	/**
	 * Add a menu to the menu bar.
	 * 
	 * @param menu
	 */
	public void addMainMenu(ActionMenu menu);
	
	/**
	 * Add a sub menu to a menu on the menu bar.
	 * 
	 * @param menuId The id of the menu to add to.
	 * @param group
	 * @param menu
	 */
	public void addSubMenu(String menuId, String group, ActionMenu menu);

	/**
	 * At a sub menu to the pop-up menu for given group. If a menu
	 * has already been registered by the given menu's id then the previous
	 * definition may be re-used.
	 * 
	 * @param group
	 * @param menu
	 */
	public void addContextSubMenu(String group, ActionMenu menu);
	
	/**
	 * Add a menu item for an action.
	 * 
	 * @param menuId
	 * @param group
	 * @param action
	 */
	public void addMenuItem(String menuId, String group, ArooaAction action);

	/**
	 * Add a pop-up menu item for an action.
	 * 
	 * @param group
	 * @param action
	 */
	public void addContextMenuItem(String group, ArooaAction action);
	
}
