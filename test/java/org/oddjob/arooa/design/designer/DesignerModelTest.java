package org.oddjob.arooa.design.designer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

import java.util.concurrent.atomic.AtomicReference;

import javax.swing.tree.TreeModel;

import org.junit.Test;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.design.DesignComponent;
import org.oddjob.arooa.design.DesignComponentBase;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignListener;
import org.oddjob.arooa.design.DesignNotifier;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.DesignProperty;
import org.oddjob.arooa.design.DesignSeedContext;
import org.oddjob.arooa.design.DesignStructureEvent;
import org.oddjob.arooa.design.DesignValueBase;
import org.oddjob.arooa.design.IndexedDesignProperty;
import org.oddjob.arooa.design.InstanceSupport;
import org.oddjob.arooa.design.Unknown;
import org.oddjob.arooa.design.etc.UnknownInstance;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.QTag;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

public class DesignerModelTest {

	String EOL = System.getProperty("line.separator");
	
	public static class Snack {
		
	}

	public static class Apple {
		
	}
	
	private class OurDesignFactory implements DesignFactory {
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new OurDesignComponent(element, parentContext);
		}
	}
	
	private class OurDesignComponent extends DesignComponentBase {
		
		public OurDesignComponent(ArooaElement element, ArooaContext parentContext) {
			super(element, parentContext);
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] {};
		}
		
		public Form detail() {
			throw new RuntimeException("Unexpected.");
		}
	}
	
	class RootConfigHelper implements DesignNotifier {
		DesignComponent doc;
		
		RootConfigHelper(DesignComponent doc) {
			this.doc = doc;
		}
		
		public void addDesignListener(DesignListener listener) {
			listener.childAdded(new DesignStructureEvent(this, doc, 0));
		}
		
		public void removeDesignListener(DesignListener listener) {
		}
		
		public DesignComponent getDesign() {
			return doc;
		}
	}
	
    @Test
	public void testReplaceRootXML() throws Exception {

		DesignParser parser = new DesignParser(
				new StandardArooaSession(), new OurDesignFactory());

		XMLConfiguration config = new XMLConfiguration("TEST", "<snack/>");
		
		ConfigurationHandle handle = parser.parse(config);
		
		DesignerModel model = new DesignerModel(parser);

		DesignTreeNode rootTreeNode = 
			(DesignTreeNode) model.getTreeModel().getRoot();
		
		model.setCurrentSelection(rootTreeNode);

		model.viewSelectedAsXML();
		
		UnknownInstance unknown = (UnknownInstance) model.getCurrentComponent();

		assertThat(unknown.getXml(), isSimilarTo("<snack/>" + EOL));
		
		// Sanity check the root is connected to the original
		// root context.
		
		ArooaContext root2context = unknown.getArooaContext();
		ArooaContext parentContext = root2context.getParent();
		
		assertNotNull(parentContext);
		
		assertEquals(0, parentContext.getConfigurationNode().indexOf(
				root2context.getConfigurationNode()));
		
		// now check the save
		
		unknown.setXml("<lunch/>");
		
		final AtomicReference<String > savedXML = new AtomicReference<String>();
		config.setSaveHandler(new XMLConfiguration.SaveHandler() {
			@Override
			public void acceptXML(String xml) {
				savedXML.set(xml);
			}
		});
		
		handle.save();
		
		assertThat(savedXML.get(), isSimilarTo("<lunch/>" + EOL));
	}
	
	private class OurDescriptor extends MockArooaDescriptor {
		
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
						if (element.getTag().equals("snack")) {
							return new SimpleArooaClass(Snack.class);
						}
						if (element.getTag().equals("apple")) {
							return new SimpleArooaClass(
									Apple.class); // apple is a value this is for the bad type test.
						}
						throw new RuntimeException("Unexpected " + element);
					}
					
					@Override
					public ArooaElement[] elementsFor(
							InstantiationContext parentContext) {
						return new ArooaElement[] { new ArooaElement("snack") };
					}
					
					@Override
					public DesignFactory designFor(ArooaElement element,
							InstantiationContext parentContext) {
						if (element.getTag().equals("snack")) {
							return new SnackDF();
						}
						if (element.getTag().equals("apple")) {
							return new AppleDF();
						}
						
						throw new RuntimeException("Unexpected " + element);
					}
				}, new MockElementMappings() {
					@Override
					public ArooaClass mappingFor(ArooaElement element,
							InstantiationContext propertyContext) {
						assertEquals("apple", element.getTag());
						return new SimpleArooaClass(Apple.class);
					}								
				});
		}
		
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass classIdentifier, PropertyAccessor accessor) {
			if (new SimpleArooaClass(
					Snack.class).equals(classIdentifier)) {
				return new MockArooaBeanDescriptor() {
					@Override
					public String getComponentProperty() {
						return "fruit";
					}
				};
			}
			else if (new SimpleArooaClass(
					Apple.class).equals(classIdentifier)) {
				return new MockArooaBeanDescriptor() {
				};				
			}
			throw new RuntimeException("Unexpected: " + classIdentifier);
		}
	}
	
	public static class SnackDF implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new SnackDesign(element, parentContext);
		}
	}
	
	static class SnackDesign extends DesignComponentBase {

		final IndexedDesignProperty fruit;
		
		SnackDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, parentContext);
			
			fruit = new IndexedDesignProperty(
					"fruit", Object.class, ArooaType.COMPONENT, this);
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] { fruit };
		}

		public Form detail() {
			throw new RuntimeException("Unexpected.");
		}
	}
	
	
   @Test
	public void testAddRemoveChild() throws ArooaParseException {
		
		DesignSeedContext context = new DesignSeedContext(
				ArooaType.COMPONENT,
				new StandardArooaSession(new OurDescriptor()));
		
		SnackDesign root = new SnackDesign(
				new ArooaElement("snack"), context);
				
		DesignerModel model = new DesignerModel(
				new RootConfigHelper(root));

		TreeModel treeModel = model.getTreeModel(); 

		assertEquals(0, treeModel.getChildCount(treeModel.getRoot()));
		
		InstanceSupport support = new InstanceSupport(root.fruit);

		support.insertTag(0, new QTag("snack"));
		
		assertEquals(1, treeModel.getChildCount(treeModel.getRoot()));

		support.insertTag(0, new QTag("snack"));
		
		assertEquals(2, treeModel.getChildCount(treeModel.getRoot()));
		
		support.removeInstance(root.fruit.instanceAt(0));
		
		assertEquals(1, treeModel.getChildCount(treeModel.getRoot()));
	}
	
	
   @Test
	public void testAddingBuiltStructure() throws ArooaParseException {
		
		DesignSeedContext context = new DesignSeedContext(
				ArooaType.COMPONENT,
				new StandardArooaSession(new OurDescriptor()));
		
		SnackDesign root = new SnackDesign(
				new ArooaElement("snack"), context);
				
		InstanceSupport support = new InstanceSupport(root.fruit);

		support.insertTag(0, new QTag("snack"));
		support.insertTag(0, new QTag("snack"));

		DesignerModel model = new DesignerModel(
				new RootConfigHelper(root));

		TreeModel treeModel = model.getTreeModel(); 

		assertEquals(2, treeModel.getChildCount(treeModel.getRoot()));

	}
	
	public static class AppleDF implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new AppleDesign(element, parentContext);
		}
	}
	
	private static class AppleDesign extends DesignValueBase {

		AppleDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, parentContext);
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] { };
		}

		public Form detail() {
			throw new RuntimeException("Unexpected.");
		}
	}
	
	/**
	 * Check that the added child must be a DesignComponent.
	 * @throws ArooaParseException 
	 */
   @Test
	public void testAddingNonComponentChild() throws ArooaParseException {
		
		DesignSeedContext context = new DesignSeedContext(
				ArooaType.COMPONENT,
				new StandardArooaSession(new OurDescriptor()));
		
		SnackDesign root = new SnackDesign(
				new ArooaElement("snack"), context);
				
		DesignerModel model = new DesignerModel(
				new RootConfigHelper(root));

		TreeModel treeModel = model.getTreeModel(); 

		assertEquals(0, treeModel.getChildCount(treeModel.getRoot()));
		
		InstanceSupport support = new InstanceSupport(root.fruit);

		try {
			support.insertTag(0, new QTag("apple"));
			
			fail("This shouldn't happen");
			
		} catch (IllegalStateException e) {
			
			assertTrue(e.getMessage().startsWith(("[apple] is not a DesignComponent for child of [snack]")));
		}
		
	}
	
	
   @Test
	public void testReplaceSelectedConfiguration() throws ArooaParseException {
		
		DesignSeedContext context = new DesignSeedContext(
				ArooaType.COMPONENT,
				new StandardArooaSession(new OurDescriptor()));
		
		SnackDesign root = new SnackDesign(
				new ArooaElement("snack"), context);
		
		InstanceSupport support = new InstanceSupport(root.fruit);
		support.insertTag(0, new QTag("snack"));
		support.insertTag(0, new QTag("snack"));
				
		DesignerModel model = new DesignerModel(
				new RootConfigHelper(root));

		TreeModel treeModel = model.getTreeModel();
				
		DesignTreeNode child = (DesignTreeNode) treeModel.getChild(treeModel.getRoot(), 1);
		
		model.setCurrentSelection(child);
		
		model.replaceSelected(new XMLConfiguration("TEST", "<snack/>"));
		
		DesignTreeNode newChild = (DesignTreeNode) treeModel.getChild(
				treeModel.getRoot(), 1);

		assertEquals(2, treeModel.getChildCount(treeModel.getRoot()));
		assertTrue(newChild != child);
		
		assertTrue(model.getCurrentSelection() == newChild);
	}

	// need to do this because the anonymous class ConfiguHelper
	// can't refer to a local model.
	DesignerModel model;
	
   @Test
	public void testReplaceSelectedRootConfiguration() throws ArooaParseException {

		final DesignParser parser = new DesignParser(
				new StandardArooaSession(new OurDescriptor()),
				new SnackDF());
		parser.setArooaType(ArooaType.COMPONENT);
		
		parser.parse(new XMLConfiguration("TEST", "<snack/>"));
		
		model = new DesignerModel(parser);

		TreeModel treeModel = model.getTreeModel();

		DesignTreeNode originalRoot = (DesignTreeNode) treeModel.getRoot();
		
		model.setCurrentSelection(
				originalRoot);
		
		model.replaceSelected(new XMLConfiguration("TEST", "<snack/>"));
		
		assertTrue(model.getCurrentSelection() == treeModel.getRoot());
		
		// The root should change.
		assertFalse(originalRoot == treeModel.getRoot());

		// Sanity check by putting The original back 
		
		model.replaceSelected(originalRoot.getDesignComponent().getArooaContext().getConfigurationNode());
		
		assertTrue(model.getCurrentSelection() == treeModel.getRoot());
	}
	
   @Test
	public void testViewSelectedAsXML() throws Exception {
		
		DesignSeedContext context = new DesignSeedContext(
				ArooaType.COMPONENT,
				new StandardArooaSession(new OurDescriptor()));
		
		SnackDesign root = new SnackDesign(
				new ArooaElement("snack"), context);
		InstanceSupport support = new InstanceSupport(root.fruit);
		support.insertTag(0, new QTag("snack"));
		support.insertTag(0, new QTag("snack"));
				
		DesignerModel model = new DesignerModel(
				new RootConfigHelper(root));

		TreeModel treeModel = model.getTreeModel();
		

		DesignTreeNode child = (DesignTreeNode) treeModel.getChild(treeModel.getRoot(), 1);
		
		model.setCurrentSelection(child);
		
		model.viewSelectedAsXML();
		
		assertEquals(2, treeModel.getChildCount(treeModel.getRoot()));

		DesignTreeNode newChild = (DesignTreeNode) treeModel.getChild(treeModel.getRoot(), 1);

		assertTrue(model.getCurrentSelection() == newChild);
		
		Unknown unknown = (Unknown) newChild.getDesignComponent(); 
		
		assertThat(unknown.getXml(), isSimilarTo("<snack/>" + EOL));
	}
}
