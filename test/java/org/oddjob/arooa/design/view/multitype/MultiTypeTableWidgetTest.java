package org.oddjob.arooa.design.view.multitype;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Action;
import javax.swing.ActionMap;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.deploy.ConfigurationDescriptorFactory;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.DesignProperty;
import org.oddjob.arooa.design.DesignValueBase;
import org.oddjob.arooa.design.MappedDesignProperty;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.MultiTypeTable;
import org.oddjob.arooa.design.screem.StandardForm;
import org.oddjob.arooa.design.view.ViewMainHelper;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.QTag;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.xml.sax.SAXException;

public class MultiTypeTableWidgetTest extends XMLTestCase {

	private static final ArooaElement APPLE;
	
	
	static {
		try {
			APPLE = new ArooaElement(new URI("oddjob:fruit"), "apple");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public interface Fruit {		
	}
	
	public static class Apple implements Fruit {
		public void setColour(String colour) {}
		
	}
	
	public static class Orange implements Fruit {
		public void setSeedless(boolean seedless) {}
	}
		
	public static class Snack {
		
		public void setFruit(String name, Fruit fruit) {
			
		}
	}
	
	public static class SnackDF implements DesignFactory {
		@Override
		public DesignInstance createDesign(ArooaElement element,
				ArooaContext parentContext) throws ArooaPropertyException {
			return new SnackDesign(element, parentContext);
		}
	}
	
	private static class SnackDesign extends DesignValueBase {
		
		private final MappedDesignProperty fruit;
		
		public SnackDesign(ArooaElement element,
				ArooaContext parentContext) {
			super(element, parentContext);
			
			fruit = new MappedDesignProperty("fruit", this);
		}

		@Override
		public Form detail() {
			return new StandardForm("Fruits", this)
				.addFormItem(fruit.view());
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] { fruit };
		}
	}
	
	String xml = 
			"<arooa:descriptor  xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'" +
			" prefix='fruit' namespace='oddjob:fruit'>" +
			" <values>" +
			"  <arooa:bean-def element='apple'" +
			"    className='org.oddjob.arooa.design.view.multitype.MultiTypeTableWidgetTest$Apple'/>" +
			"  <arooa:bean-def element='orange'" +
			"    className='org.oddjob.arooa.design.view.multitype.MultiTypeTableWidgetTest$Orange'/>" +
			"  <arooa:bean-def element='snack'" +
			"    className='org.oddjob.arooa.design.view.multitype.MultiTypeTableWidgetTest$Snack'" +
			"    designFactory='org.oddjob.arooa.design.view.multitype.MultiTypeTableWidgetTest$SnackDF'/>" +
			" </values>" +
			"</arooa:descriptor>";
	
	ArooaDescriptor createDescriptor() {
		return new ConfigurationDescriptorFactory(
				new XMLConfiguration("XML", xml)).createDescriptor(
						getClass().getClassLoader());
	}
		
	SnackDesign design;
	
	String EOL = System.getProperty("line.separator");
	
	public void testSwapActions() throws ArooaParseException, SAXException, IOException {
		
		DesignParser designParser = new DesignParser(
				new StandardArooaSession(createDescriptor()));
		
		String xml = 
				"<fruit:snack xmlns:fruit='oddjob:fruit'>" +
				" <fruit>" +
				"  <fruit:apple key='morning' colour='red'/>" +
				"  <fruit:orange key='afternoon' seedless='true'/>" +
				" </fruit>" +
				"</fruit:snack>";
		
		designParser.parse(new XMLConfiguration("XML", xml));
		
		this.design = (SnackDesign) designParser.getDesign();

		MultiTypeTable viewModel = (MultiTypeTable)
				design.children()[0].view();
		
		MultiTypeDesignModel model = new MultiTypeDesignModel(viewModel);
				
		MultiTypeTableWidget test = new MultiTypeTableWidget(model,
				MultiTypeStrategy.Strategies.KEYED);
		
		assertEquals(-1, test.getSelectedRow());
		assertEquals(2, model.getRowCount());
		
		ActionMap actionMap = test.getActionMap();
		
		Action up = actionMap.get(
				MultiTypeTableWidget.SWAP_UP_ACTION_COMMAND);
		Action down = actionMap.get(
				MultiTypeTableWidget.SWAP_DOWN_ACTION_COMMAND);
		
		assertEquals(-1, test.getSelectedRow());
		
		assertEquals(false, up.isEnabled());
		assertEquals(false, down.isEnabled());
		
		QTag appleTag = new QTag(APPLE, design.getArooaContext());
		
		test.setSelectedRow(2);
		
		assertEquals(false, up.isEnabled());
		assertEquals(false, down.isEnabled());
		
		model.createRow(appleTag, 2);
		assertEquals(2, test.getSelectedRow());
		
		model.getRow(2).setName("extra");
		
		assertEquals(true, up.isEnabled());
		assertEquals(false, down.isEnabled());
										
		up.actionPerformed(null);
		
		assertEquals(1, test.getSelectedRow());
		
		assertEquals(true, up.isEnabled());
		assertEquals(true, down.isEnabled());
		
		up.actionPerformed(null);
		
		assertEquals(0, test.getSelectedRow());
		
		assertEquals(false, up.isEnabled());
		assertEquals(true, down.isEnabled());
		
		down.actionPerformed(null);
		
		assertEquals(1, test.getSelectedRow());
		
		assertEquals(true, up.isEnabled());
		assertEquals(true, down.isEnabled());
		
		XMLArooaParser parser = new XMLArooaParser();
		
		parser.parse(design.getArooaContext().getConfigurationNode());
		
		String expected = 
				"<fruit:snack xmlns:fruit='oddjob:fruit'>" + EOL +
				" <fruit>" + EOL +
				"  <fruit:apple key='morning' colour='red'/>" + EOL +
				"  <fruit:apple key='extra'/>" + EOL +
				"  <fruit:orange key='afternoon' seedless='true'/>" + EOL +
				" </fruit>" + EOL +
				"</fruit:snack>" + EOL; 
		
		XMLUnit.setIgnoreWhitespace(true);
		assertXMLEqual(expected, parser.getXml());
	}
	
	public static void main(String[] args) throws ArooaParseException, SAXException, IOException {
		MultiTypeTableWidgetTest test = new MultiTypeTableWidgetTest();
		test.testSwapActions();
		
		ViewMainHelper view = new ViewMainHelper(test.design);
		view.run();
	}
}
