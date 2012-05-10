package org.oddjob.arooa.design.designer;

import javax.swing.Action;

import junit.framework.TestCase;

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
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ViewActionsContributorTest extends TestCase {

	public static class Stuff {
		
	}
	
	private class MyDescriptor extends MockArooaDescriptor {
	
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
							InstantiationContext propertyContext) {
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
	
	private class MyDesignFactory implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new MyDesign(element, parentContext);
		}
	}
	
	private class MyDesign extends DesignComponentBase {
		
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

	private class OurActions implements ActionRegistry {
		
		Action action;
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
			assertEquals(ViewActionsContributor.VIEW_GROUP, group);
			
			this.action = action;
		}
		
		public void addSubMenu(String menuId, String group, ActionMenu menu) {
			throw new RuntimeException("Unexpected.");
		}
	}

	
	public void testRootActions() throws ArooaParseException {
	
		DesignParser parser = new DesignParser(
				new StandardArooaSession(new MyDescriptor()),
				new MyDesignFactory());
		parser.setArooaType(ArooaType.COMPONENT);
		
		parser.parse(new XMLConfiguration("TEST", "<stuff/>"));

		assertTrue(parser.getDesign() instanceof MyDesign);
		
		DesignerModel model = new DesignerModel(parser);
		
		ViewActionsContributor test = new ViewActionsContributor(model);
		
		OurActions actions = new OurActions();
		
		test.contributeTo(actions);
		
		assertEquals(ViewActionsContributor.VIEW_MENU_ID, actions.menu.getId());

		Action toggle = actions.action;
		
		assertFalse(toggle.isEnabled());
		
		DesignTreeNode root = model.getTreeModel().getRoot();
		model.setCurrentSelection(root);
		
		assertTrue(toggle.isEnabled());
		
		toggle.actionPerformed(null);

		// Note that the parser hasn't changed but current has
		assertTrue(parser.getDesign() instanceof MyDesign);
		assertTrue(model.getCurrentComponent() instanceof Unknown);
		
		toggle.actionPerformed(null);
		
		assertTrue(parser.getDesign() instanceof MyDesign);
		
		// Sanity check that the root has changed.
		assertEquals(model.getCurrentComponent(), parser.getDesign());
		
	}
	
}
