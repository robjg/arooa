package org.oddjob.arooa.design.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.oddjob.arooa.design.designer.MenuProvider;

/**
 * Organises a Hierarchy of menus for a Swing view.
 * 
 * @author rob
 *
 */
public class ConfigurableMenus 
implements ActionRegistry, MenuProvider {
	
	private final List<String> menuBarIds
		= new ArrayList<String>();
	
	private final Map<String, SubMenu> menus = 
		new LinkedHashMap<String, SubMenu>();

	private MenuThings contextMenu = new MenuThings();
	

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.design.actions.ActionRegistry#addContextMenuItem(java.lang.String, javax.swing.Action)
	 */
	public void addContextMenuItem(String group, final ArooaAction action) {
		contextMenu.addMenuThing(group, new MenuThing() {
			public void addTo(MenuFacade menu) {
				menu.add(action);
			}
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.design.actions.ActionRegistry#addContextSubMenu(java.lang.String, org.oddjob.arooa.design.actions.ActionMenu)
	 */
	public void addContextSubMenu(String group, ActionMenu menu) {
		
		final SubMenu subMenu;
		if (menus.containsKey(menu.getId())) {
			subMenu = menus.get(menu.getId());
		}
		else { 
			subMenu = new SubMenu(menu);
			menus.put(menu.getId(), subMenu);
		}
		
		contextMenu.addMenuThing(group, new MenuThing() {
			public void addTo(MenuFacade menu) {
				menu.add(subMenu.getJMenu());
			}
		});
	}
	
	public void addMainMenu(ActionMenu menu) {
		if (!menus.containsKey(menu.getId())) {
			SubMenu subMenu = new SubMenu(menu);
			menus.put(menu.getId(), subMenu);
		}
		
		menuBarIds.add(menu.getId());
	}
	
	public void addMenuItem(String menuId, String group, final ArooaAction action) {

		SubMenu subMenu = menus.get(menuId);
		
		if (subMenu == null) {
			throw new IllegalArgumentException("No menu with id: " + menuId);
		}
		
		
		subMenu.addMenuThing(group, new MenuThing() {
			public void addTo(MenuFacade menu) {
				menu.add(action);
			}
		});		
	}
	
	public void addSubMenu(String menuId, String group, ActionMenu menu) {
		
		final SubMenu subMenu;
		
		if (menus.containsKey(menu.getId())) {
			subMenu = menus.get(menu.getId()); 
		}
		else {
			subMenu = new SubMenu(menu);
		}
		
		menus.put(menu.getId(), subMenu);
		
		SubMenu addToMenu = menus.get(menuId);
		
		addToMenu.addMenuThing(group, new MenuThing() {
			public void addTo(MenuFacade menu) {
				menu.add(subMenu.getJMenu());
			}
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.design.designer.MenuProvider#getJMenuBar()
	 */
	public JMenu[] getJMenuBar() {
		
		JMenu[] mainMenus = new JMenu[menuBarIds.size()];
		int i= 0;
		
		for (String menuBarId : menuBarIds ) {
			
			
			SubMenu subMenu = menus.get(menuBarId);
			
			mainMenus[i++] = subMenu.getJMenu();
		}
		
		return mainMenus;
	}
	
	public JPopupMenu getPopupMenu() {
		JMenu popup = new JMenu();

		contextMenu.addTo(popup);
		
		return popup.getPopupMenu();
		
	}
	
	/**
	 * Stands between the Swing JMenu and our menu things
	 * so we can capture the components.
	 * 
	 * @author rob
	 *
	 */
	interface MenuFacade {
		
		void add(ArooaAction action);
		
		void add(JMenu subMenu);
	}
	
	/**
	 * Either an item or a sub menu.
	 * @author rob
	 *
	 */
	interface MenuThing {
		
		void addTo(MenuFacade menu);
	}
		
	/**
	 * Map Groups to MenuThing.
	 * 
	 * @author rob
	 *
	 */
	class MenuThings {
	
		private final Map<String, List<MenuThing>> groups = 
			new LinkedHashMap<String, List<MenuThing>>();
					
		/**
		 * Add either a sub menu or an item.
		 * 
		 * @param group
		 * @param thing
		 */
		void addMenuThing(String group, MenuThing thing) {
			List<MenuThing> things = groups.get(group);
			if (things == null) {
				things = new ArrayList<MenuThing>();
				groups.put(group, things);
			}
			things.add(thing);
		}		
		
		/**
		 * Add groups to a menu.
		 * 
		 * @param jmenu
		 */
		void addTo(JMenu jmenu) {
			
			MagicSeparator last = null;
			for (String group: groups.keySet()) {
				MagicSeparator separator = new MagicSeparator(jmenu);
				if (last != null) {
					last.next = separator;
					last.init();
				}
				
				for (MenuThing thing: groups.get(group)) {
					
					thing.addTo(separator);
				}
				last = separator;
			}
			if (last != null) {
				last.init();
			}
		}
	}
	
	class MagicSeparator extends JSeparator implements MenuFacade {
		private static final long serialVersionUID = 20090805;
		
		private final List<ArooaAction> actions = 
			new ArrayList<ArooaAction>();
		
		private final JMenu realMenu;
		
		private MagicSeparator next;
		
		private boolean[] visibles;

		MagicSeparator(JMenu theMenu) {
			this.realMenu = theMenu;
		}
		
		public void add(ArooaAction action) {
			actions.add(action);
			final JMenuItem item = realMenu.add(action);
			item.setVisible(action.isVisible());
			action.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if (ArooaAction.VISIBLE_PROPERTY.equals(evt.getPropertyName())) {
						item.setVisible(((Boolean) evt.getNewValue()).booleanValue());
					}
				}
			});
		}
		
		public void add(JMenu subMenu) {
			realMenu.add(subMenu);
		}

		/**
		 * Called when the group has been processed.
		 */
		void init() {
			if (next == null) {
				return;
			}

			visibles = new boolean[actions.size()];
			int index = 0;
			for (ArooaAction action: actions) {
				final int thisIndex = index++;
				visibles[thisIndex] = action.isVisible();
				action.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						if (ArooaAction.VISIBLE_PROPERTY.equals(evt.getPropertyName())) {
							visibles[thisIndex] = ((Boolean) evt.getNewValue()).booleanValue();
							evaluate();
						}
					}
				});
			}
			evaluate();
			realMenu.add(this);
		}
		
		/**
		 * Is separator visible.
		 */
		private void evaluate() {
			boolean current = isVisible();
			boolean now = false;
			for (boolean visible: visibles) {
				if (visible) {
					now = true;
				}
			}
			if (now != current) {
				setVisible(now);
			}
		}
	}
	
	/**
	 * A sub menu.
	 * 
	 */
	class SubMenu extends MenuThings {
		
		private final ActionMenu actionMenu;
		
		SubMenu(ActionMenu actionMenu) {
			this.actionMenu = actionMenu;
		}
		
		JMenu getJMenu() {
			
			JMenu menu = new JMenu(actionMenu.getName());
			menu.setMnemonic(actionMenu.getMnumonic());

			addTo(menu);
			
			return menu;
		}
	}
}
