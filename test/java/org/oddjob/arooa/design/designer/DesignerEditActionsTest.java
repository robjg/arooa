package org.oddjob.arooa.design.designer;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;

import org.junit.Assert;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.design.DesignComponentBase;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.DesignProperty;
import org.oddjob.arooa.design.SimpleDesignProperty;
import org.oddjob.arooa.design.Unknown;
import org.oddjob.arooa.design.actions.ActionMenu;
import org.oddjob.arooa.design.actions.ActionRegistry;
import org.oddjob.arooa.design.actions.ArooaAction;
import org.oddjob.arooa.design.actions.EditActionsContributor;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

public class DesignerEditActionsTest extends Assert {

	public static class Stuff {
		
	}
	
	class MyDescriptor extends MockArooaDescriptor {
	
		boolean hasChildren;
		
		@Override
		public ConversionProvider getConvertletProvider() {
			return null;
		}
		
		@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(new MockElementMappings() {
					@Override
					public ArooaClass mappingFor(ArooaElement element,
							InstantiationContext parentContext) {
						assertEquals("stuff", element.getTag());
						return new SimpleArooaClass(Stuff.class);
					}
				}, null);
		}
		
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass classIdentifier, PropertyAccessor accessor) {
			assertEquals(new SimpleArooaClass(Stuff.class), 
					classIdentifier);
			return new MockArooaBeanDescriptor() {
				@Override
				public String getComponentProperty() {
					if (hasChildren) {
						return "stuff";
					}
					else {
						return null;
					}
				}
			};
		}
	}
	
	class MyDesignFactory implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new MyDesign(element, parentContext);
		}
	}
	
	class MyDesign extends DesignComponentBase {
		
		private final SimpleDesignProperty child;
				
		public MyDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, parentContext);
			
			child = new SimpleDesignProperty(
					"one", Object.class, ArooaType.COMPONENT, this);
			
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] { child };
		}
		
		public Form detail() {
			throw new RuntimeException("Unexpected");
		}
	}

	class OurActions implements ActionRegistry {
		
		Map<Object, Action> actions = 
			new HashMap<Object, Action>();
		
		ActionMenu menu;
		
		public void addContextMenuItem(String group, ArooaAction action) {
		}
		
		public void addContextSubMenu(String group, ActionMenu menu) {
			throw new RuntimeException("Unexpected.");
		}
		
		public void addMainMenu(ActionMenu menu) {
			this.menu = menu;
		}
		
		public void addMenuItem(String menuId, String group, ArooaAction action) {
			assertEquals(menu.getId(), menuId);
			assertEquals(EditActionsContributor.EDIT_GROUP, group);
			
			actions.put(action.getValue(Action.ACTION_COMMAND_KEY), action);
		}
		
		public void addSubMenu(String menuId, String group, ActionMenu menu) {
			throw new RuntimeException("Unexpected.");
		}
	}

	
   @Test
	public void testRootActions() throws ArooaParseException {
	
		DesignParser parser = new DesignParser(
				new StandardArooaSession(new MyDescriptor()),
				new MyDesignFactory());
		parser.setArooaType(ArooaType.COMPONENT);
		
		parser.parse(new XMLConfiguration("TEST", "<stuff/>"));
		
		DesignerModel model = new DesignerModel(parser);
		
		DesignTreeNode root = model.getTreeModel().getRoot();
		model.setCurrentSelection(root);
		
		DesignerEditActions test = new DesignerEditActions(model);
		
		OurActions actions = new OurActions();
		
		test.contributeTo(actions);
		
		assertEquals(EditActionsContributor.EDIT_MENU_ID, actions.menu.getId());
		
		Action cut = actions.actions.get("cut");
		
		assertFalse(cut.isEnabled());
		
		Action copy = actions.actions.get("copy");
		
		assertTrue(copy.isEnabled());
		
		Action paste = actions.actions.get("paste");
		
		assertFalse(paste.isEnabled());
		
		Action delete = actions.actions.get("delete");
		
		assertFalse(delete .isEnabled());
	}
	
   @Test
	public void testRootActionsWithChildren() throws ArooaParseException {
		
		MyDescriptor descriptor = new MyDescriptor();
		descriptor.hasChildren = true;
		
		DesignParser parser = new DesignParser(
				new StandardArooaSession(descriptor),
				new MyDesignFactory());
		parser.setArooaType(ArooaType.COMPONENT);
		
		parser.parse(new XMLConfiguration("TEST", "<stuff/>"));
		
		DesignerModel model = new DesignerModel(parser);
		
		DesignTreeNode root = model.getTreeModel().getRoot();
		model.setCurrentSelection(root);
		
		DesignerEditActions test = new DesignerEditActions(model);
		
		OurActions actions = new OurActions();
		
		test.contributeTo(actions);
		
		assertEquals(EditActionsContributor.EDIT_MENU_ID, actions.menu.getId());
		
		Action cut = actions.actions.get("cut");
		
		assertFalse(cut.isEnabled());
		
		Action copy = actions.actions.get("copy");
		
		assertTrue(copy.isEnabled());
		
		Action paste = actions.actions.get("paste");
		
		assertTrue(paste.isEnabled());
		
		Action delete = actions.actions.get("delete");
		
		assertFalse(delete .isEnabled());
	}
	
	
   @Test
	public void testUnknownActions() throws ArooaParseException {

		DesignParser parser = new DesignParser(
				new StandardArooaSession());
		
		parser.parse(new XMLConfiguration("TEST", "<stuff/>"));
		
		assertTrue(parser.getDesign() instanceof Unknown);
		
		DesignerModel model = new DesignerModel(parser);
		
		DesignTreeNode root = model.getTreeModel().getRoot();
		model.setCurrentSelection(root);
		
		DesignerEditActions test = new DesignerEditActions(model);
		
		OurActions actions = new OurActions();
		
		test.contributeTo(actions);
		
		assertEquals(EditActionsContributor.EDIT_MENU_ID, actions.menu.getId());
		
		Action cut = actions.actions.get("cut");
		
		assertFalse(cut.isEnabled());
		
		Action copy = actions.actions.get("copy");
		
		assertTrue(copy.isEnabled());
		
		Action paste = actions.actions.get("paste");
		
		assertFalse(paste.isEnabled());
		
		Action delete = actions.actions.get("delete");
		
		assertFalse(delete .isEnabled());
		
	}
}
