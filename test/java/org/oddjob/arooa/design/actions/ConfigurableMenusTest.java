package org.oddjob.arooa.design.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;

import junit.framework.TestCase;

public class ConfigurableMenusTest extends TestCase {

	class OurAction extends AbstractArooaAction {
		private static final long serialVersionUID = 1;
		
		OurAction(String name) {
			super(name);
		}
		
		public void actionPerformed(ActionEvent e) {
		}
		
		@Override
		public String toString() {
			return (String) getValue(NAME);
		}
	}
	
	class SimpleActions implements ActionContributor {

		OurAction apple = new OurAction("Apple");
		ArooaAction orange = new OurAction("Orange");
		
		public void contributeTo(ActionRegistry actionRegistry) {
			
			actionRegistry.addMainMenu(new ActionMenu("FRUIT", "Fruit"));
			
			actionRegistry.addMenuItem("FRUIT", "first", apple);
			actionRegistry.addMenuItem("FRUIT", "second", orange);
		}
	}
	
	public void testSimple() {
		
		test = new ConfigurableMenus();
		
		ActionContributor actions = new SimpleActions();
		
		actions.contributeTo(test);
		
		JMenu resultMenu[] = test.getJMenuBar();
		
		JMenu foodMenu = resultMenu[0];
		
		assertEquals("Fruit", foodMenu.getActionCommand());
		
		Component[] subComponents = foodMenu.getMenuComponents();
		
		assertEquals(3, subComponents.length);
		
		assertTrue(subComponents[1] instanceof JSeparator);
		
		assertTrue(subComponents[1].isVisible());

	}
	
	public void testNonVisible() {
		
		test = new ConfigurableMenus();
		
		SimpleActions actions = new SimpleActions();
		
		actions.contributeTo(test);
		
		JMenu resultMenu[] = test.getJMenuBar();
		
		JMenu foodMenu = resultMenu[0];
		
		assertEquals("Fruit", foodMenu.getActionCommand());
		
		Component[] subComponents = foodMenu.getMenuComponents();
		
		assertEquals(3, subComponents.length);
		
		assertTrue(subComponents[1] instanceof JSeparator);
		
		actions.apple.setVisible(false);
		
		assertFalse(subComponents[0].isVisible());
		assertFalse(subComponents[1].isVisible());
		assertTrue(subComponents[2].isVisible());
	
		// test again.
		resultMenu = test.getJMenuBar();
	
		foodMenu = resultMenu[0];
		
		subComponents = foodMenu.getMenuComponents();
		
		assertFalse(subComponents[0].isVisible());
		assertFalse(subComponents[1].isVisible());
		assertTrue(subComponents[2].isVisible());
		
	}
	
	class ComplexActions implements ActionContributor {

		ArooaAction apple = new OurAction("Apple");
		ArooaAction orange = new OurAction("Orange");
		
		ArooaAction cabbage = new OurAction("Cabbage");
		
		public void contributeTo(ActionRegistry actionRegistry) {
			
			actionRegistry.addMainMenu(new ActionMenu("FOOD", "Food"));
			
			actionRegistry.addSubMenu(
					"FOOD", "a",  new ActionMenu("FRUIT", "Fruit"));
						
			actionRegistry.addSubMenu(
					"FOOD", "b", new ActionMenu("VEG", "Veg"));
			
			actionRegistry.addContextSubMenu(
					"others", new ActionMenu("FRUIT", "Fruit"));
						
			actionRegistry.addContextSubMenu(
					"others", new ActionMenu("VEG", "Veg"));
			
			actionRegistry.addMenuItem("FRUIT", "others", apple);
			actionRegistry.addMenuItem("FRUIT", "others", orange);
			actionRegistry.addMenuItem("VEG", "others", cabbage);
		}
	}
	
	ConfigurableMenus test;
	
	public void testMenus() {
		
		test = new ConfigurableMenus();
		
		ActionContributor actions = new ComplexActions();
		
		actions.contributeTo(test);
		
		JMenu resultMenu[] = test.getJMenuBar();
		
		JMenu foodMenu = resultMenu[0];
		
		assertEquals("Food", foodMenu.getActionCommand());
		
		Component[] subComponents = foodMenu.getMenuComponents();
		
		assertEquals(3, subComponents.length);
		
//		assertEquals("Fruit", ((JMenu) subComponents[0]).getActionCommand());
//		assertTrue(subComponents[1] instanceof JSeparator);
//		assertEquals("Veg", ((JMenu) subComponents[2]).getActionCommand());
	}
	
	public static void main(String... args) {
		
		ConfigurableMenusTest test = new ConfigurableMenusTest();
		test.testSimple();
		
		JPanel panel = new JPanel();
		panel.addMouseListener(test.new PopupListener());
		
		
		JMenuBar bar = new JMenuBar();
		for (JMenu mainMenu: test.test.getJMenuBar()) {
			bar.add(mainMenu);
		}
		
		JFrame frame = new JFrame();
		
		
		frame.setJMenuBar(bar);
		frame.getContentPane().add(panel);
		frame.setLocation(300, 200);
		frame.setSize(300, 200);
		
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		frame.setVisible(true);
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
			
			test.getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
