package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockArooaTools;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.DesignProperty;
import org.oddjob.arooa.design.DesignValueBase;
import org.oddjob.arooa.design.IndexedDesignProperty;
import org.oddjob.arooa.design.MappedDesignProperty;
import org.oddjob.arooa.design.model.MockDesignInstance;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.MultiTypeTable;
import org.oddjob.arooa.design.screem.StandardForm;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.parsing.QTag;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class MultiTypeTableViewTest extends XMLTestCase {
	
	private Component view;
	
	public static class Fruit {
		
	}
	
	public static class Apple {
		public void setColour(String colour) {}
		
	}
	
	public static class Orange {
		
	}

	private static final ArooaElement APPLE;
	private static final ArooaElement ORANGE;
	
	
	static {
		try {
			APPLE = new ArooaElement(new URI("http://fruit"), "apple");
			ORANGE = new ArooaElement(new URI("http://fruit"), "orange");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private class OurMappings extends MockElementMappings {

		@Override
		public ArooaElement[] elementsFor(
				InstantiationContext propertyContext) {
			return new ArooaElement[] { APPLE, ORANGE };
		}
		
		@Override
		public DesignFactory designFor(ArooaElement element, 
				InstantiationContext propertyContext) {
			
			return null;
		}
		
		@Override
		public ArooaClass mappingFor(ArooaElement element, 
				InstantiationContext propertyContext ) {
			
			if ("apple".equals(element.getTag())) {
				return new SimpleArooaClass(Apple.class);
			}
			if ("orange".equals(element.getTag())) {
				return new SimpleArooaClass(Orange.class);
			}
			throw new RuntimeException(element.toString());
		}
	}
	
	private class OurDescriptor extends MockArooaDescriptor {
		
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass classIdentifier, PropertyAccessor accessor) {
			return null;
		}

		@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(null, new OurMappings());
		}
		
		@Override
		public String getPrefixFor(URI namespace) {
			return "fruit";
		}
		
		@Override
		public ConversionProvider getConvertletProvider() {
			return null;
		}
		
		@Override
		public ClassResolver getClassResolver() {
			return new ClassLoaderClassResolver(
					getClass().getClassLoader());
		}
	}
 
	
	private class ExistingSession extends MockArooaSession {
		@Override
		public ArooaTools getTools() {
			return new MockArooaTools() {
				@Override
				public PropertyAccessor getPropertyAccessor() {
					return new BeanUtilsPropertyAccessor();
				}
				
				@Override
				public ArooaConverter getArooaConverter() {
					return new DefaultConverter();
				}
			};
		}
		@Override
		public ArooaDescriptor getArooaDescriptor() {
			return new OurDescriptor();
		}		
	}
	
	PrefixMappings prefixMappings = new SimplePrefixMappings();
	
	{
		try {
			prefixMappings.put("fruit", new URI("http://fruit"));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	private class OurContext extends MockArooaContext {
	
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return prefixMappings;
		}
			
		@Override
		public ArooaSession getSession() {
			return new ExistingSession();
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new MockConfigurationNode() {
				@Override
				public int insertChild(ConfigurationNode child) {
					return -1;
				}
			};
		}

	}
	
	private class OurDesign extends MockDesignInstance {
		
		@Override
		public ArooaContext getArooaContext() {
			return new OurContext();
		}
	}
	
	/**
	 * Test inserting and changing cells in the table.
	 */
	public void testIndexed() {
		
		IndexedDesignProperty property = new IndexedDesignProperty(
				"fruit", Fruit.class, ArooaType.VALUE, new OurDesign()); 
		
		MultiTypeTable multiTable = new MultiTypeTable(property);
		
		MultiTypeTableView test = new MultiTypeTableView(multiTable);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		test.inline(panel, 0, 0, false);

		assertEquals(2, panel.getComponentCount());
		
		JScrollPane scrollPane = (JScrollPane) panel.getComponent(1);
		
		JTable table = (JTable) scrollPane.getViewport().getComponent(0);
		
		table.setValueAt(prefixMappings.getQName(APPLE), 0, 0);
		
		assertEquals(1, property.instanceCount());
		
		table.setValueAt(prefixMappings.getQName(ORANGE), 0, 0);
		
		assertEquals(1, property.instanceCount());
		
		table.setValueAt(prefixMappings.getQName(APPLE), 1, 0);
		
		assertEquals(2, property.instanceCount());

		table.setValueAt(new QTag(""), 1, 0);
		
		assertEquals(1, property.instanceCount());
		
		table.setValueAt(new QTag(""), 0, 0);
		
		assertEquals(0, property.instanceCount());
		
		view = panel;
	}
	
	String EOL = System.getProperty("line.separator"); 

	public void testMapped() throws Exception {
		
		MappedDesignProperty property = new MappedDesignProperty(
				"fruit", Fruit.class, ArooaType.VALUE, new OurDesign()); 
		
		MultiTypeTable multiTable = (MultiTypeTable) property.view();;
		
		MultiTypeTableView test = new MultiTypeTableView(multiTable);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		test.inline(panel, 0, 0, false);

		assertEquals(2, panel.getComponentCount());
		
		JScrollPane scrollPane = (JScrollPane) panel.getComponent(1);
		
		JTable table = (JTable) scrollPane.getViewport().getComponent(0);
		
		table.setValueAt(prefixMappings.getQName(APPLE), 0, 0);
		
		assertEquals(1, property.instanceCount());
		
		table.setValueAt("morning", 0, 1);

		XMLArooaParser parser = new XMLArooaParser();
		
		parser.parse(property.getArooaContext().getConfigurationNode());

		String expected = 
				"<fruit xmlns:fruit=\"http://fruit\">" + EOL +
				"    <fruit:apple key=\"morning\"/>" + EOL +
				"</fruit>" + EOL; 
		
		assertXMLEqual(expected, parser.getXml());
				
		table.setValueAt(prefixMappings.getQName(ORANGE), 0, 0);
		
		assertEquals(1, property.instanceCount());
		
		table.setValueAt(prefixMappings.getQName(APPLE), 1, 0);
		
		assertEquals(2, property.instanceCount());

		table.setValueAt(new QTag(""), 1, 0);
		
		assertEquals(1, property.instanceCount());
		
		table.setValueAt(new QTag(""), 0, 0);
		
		assertEquals(0, property.instanceCount());
		
		view = panel;
	}
	
	private class OurDesign2Factory implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			
			return new OurDesign2(element, parentContext);
		}
	}
	
	private class OurDesign2 extends DesignValueBase {

		IndexedDesignProperty property; 
		
		OurDesign2(ArooaElement element, ArooaContext parentContext) {
			super(element, new SimpleArooaClass(Object.class), parentContext);
			
			property = new IndexedDesignProperty(
					"fruit", Fruit.class, ArooaType.VALUE, this);
		}
		
		
		@Override
		protected DesignProperty[] children() {
			return new DesignProperty[] { property };
		}
		
		public Form detail() {
			return new StandardForm("Test", this)
			.addFormItem(property.view());
		}
	}
	
	public void testViewStartUp() throws ArooaParseException {
		
		String xml = 
			"<stuff xmlns:f='http://fruit.com'>" +
			"  <fruit>" +
			"    <f:apple/>" +
			"    <f:orange/>" +
			"  </fruit>" +
			"</stuff>";
		
		StandardArooaSession session = new StandardArooaSession(
				new OurDescriptor());
		
		DesignParser parser = new DesignParser(session, new OurDesign2Factory());

		parser.parse(new XMLConfiguration("TEST", xml));
		
		DesignInstance design = parser.getDesign();
		
		view = SwingFormFactory.create(design.detail()).dialog();
		
	}
	
	public static void main(String args[]) throws ArooaParseException {
		MultiTypeTableViewTest test = new MultiTypeTableViewTest();
		test.testViewStartUp();
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(test.view);
		frame.pack();
		frame.setVisible(true);

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	}
}
