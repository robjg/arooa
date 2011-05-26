package org.oddjob.arooa.standard;

import java.util.ArrayList;
import java.util.List;

import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class StandardConfigurationNodeTest2 extends XMLTestCase {
	
	public static class Component {
		
		List<Component> children = new ArrayList<Component>();

		String colour;
		
		@ArooaComponent
		public void setChild(int index, Component component) {
			if (component == null) {
				this.children.remove(index);				
			}
			else {
				this.children.add(index, component);
			}
		}
		
		public void setColour(String colour) {
			this.colour = colour;
		}
	}
	
	String EOL = System.getProperty("line.separator");
	
	public void testManySave() throws ArooaParseException {

		Component root = new Component();

		StandardArooaParser parser = new StandardArooaParser(root);
		
		String xml = 
			"<component>" + EOL +
			"    <child>" + EOL +
			"        <bean class=\"" + Component.class.getName() + "\"" + EOL + 
			"               colour=\"blue\"/>" + EOL + 
			"    </child>" + EOL +
			"</component>" + EOL;
		
		parser.parse(
				new XMLConfiguration("TEST", xml));
				
		ArooaContext childContext = parser.getSession(
				).getComponentPool().contextFor(root.children.get(0));

		XMLArooaParser xmlParser = new XMLArooaParser();
		
		ConfigurationHandle handle = xmlParser.parse(
				childContext.getConfigurationNode());

		ArooaContext xmlDoc = handle.getDocumentContext();
		
		CutAndPasteSupport.replace(xmlDoc.getParent(), xmlDoc, 
				new XMLConfiguration("Replace", 
						"        <bean class=\"" + Component.class.getName() + "\"" + EOL + 
						"               colour=\"green\"/>"));		
		
		handle.save();

		assertEquals("green", root.children.get(0).colour);
				
		xmlDoc = handle.getDocumentContext();
		CutAndPasteSupport.replace(xmlDoc.getParent(), xmlDoc, 
				new XMLConfiguration("Replace", 
						"        <bean class=\"" + Component.class.getName() + "\"" + EOL + 
						"               colour=\"yellow\"/>"));
		
		handle.save();

		assertEquals("yellow", root.children.get(0).colour);
	}
	
	public void testBadSave() throws Exception {

		Component root = new Component();

		StandardArooaParser parser = new StandardArooaParser(root);
		
		String xml = 
			"<component>" + EOL +
			"    <child>" + EOL +
			"        <bean class=\"" + Component.class.getName() + "\"" + EOL + 
			"               colour=\"blue\"/>" + EOL + 
			"    </child>" + EOL +
			"</component>" + EOL;
		
		parser.parse(
				new XMLConfiguration("TEST", xml));
				
		ArooaContext childContext = parser.getSession(
				).getComponentPool().contextFor(root.children.get(0));

		XMLArooaParser xmlParser = new XMLArooaParser();
		
		ConfigurationHandle handle = xmlParser.parse(
				childContext.getConfigurationNode());

		ArooaContext xmlDoc = handle.getDocumentContext();
		
		CutAndPasteSupport.replace(xmlDoc.getParent(), xmlDoc, 
				new XMLConfiguration("Replace", "<rubbish/>"));		
		
		try {
			handle.save();
			fail("Should fail.");
		} catch (Exception e) {
			// expected.
		}
		
		XMLArooaParser xmlParser2 = new XMLArooaParser();
		
		xmlParser2.parse(parser.getSession(
			).getComponentPool().contextFor(root).getConfigurationNode());
		
		
		assertXMLEqual(xml, xmlParser2.getXml());

		// Test replaced node is still useable.
		
		xmlDoc = handle.getDocumentContext();
		CutAndPasteSupport.replace(xmlDoc.getParent(), xmlDoc, 
				new XMLConfiguration("Replace", 
						"        <bean class=\"" + Component.class.getName() + "\"" + EOL + 
						"               colour=\"yellow\"/>"));
		
		handle.save();

		assertEquals("yellow", root.children.get(0).colour);
		
		// tracking down a bug where a failed save doesn't 
		// restore itself correctly...
		ArooaContext newContext = 
			parser.getSession().getComponentPool().contextFor(
				root.children.get(0));
		
		assertNotNull(newContext);
		
		ArooaContext rootContext = 
			parser.getSession().getComponentPool().contextFor(
				root);
		
		rootContext.getRuntime().destroy();
	}
	
	public void testReplaceWithBadChild() throws Exception {

		Component root = new Component();

		StandardArooaParser parser = new StandardArooaParser(root);
		
		String xml = 
			"<component>" + EOL +
			"    <child>" + EOL +
			"        <bean class=\"" + Component.class.getName() + "\"" + EOL + 
			"               colour=\"blue\"" + EOL +
			"               id=\"blue\"/>" + EOL + 
			"    </child>" + EOL +
			"</component>" + EOL;
		
		ConfigurationHandle handle = parser.parse(
				new XMLConfiguration("TEST", xml));
				
		ArooaContext rootContext = parser.getSession(
				).getComponentPool().contextFor(root);
		
		String replacementXml = 
			"<component>" + EOL +
			"    <child>" + EOL +
			"       <bean class=\"" + Component.class.getName() + "\"" +
			"              id='blue'/>" + EOL + 
			"       <rubbish/>" +
			"    </child>" + EOL +
			"</component>" + EOL;
		
		CutAndPasteSupport.ReplaceResult replaceResult = 
			CutAndPasteSupport.replace(rootContext.getParent(), rootContext, 
				new XMLConfiguration("Replace", replacementXml));
				
		assertNotNull(replaceResult.getException());
		
		XMLArooaParser xmlParser2 = new XMLArooaParser();
		
		xmlParser2.parse(parser.getSession(
			).getComponentPool().contextFor(root).getConfigurationNode());
		
		assertXMLEqual(xml, xmlParser2.getXml());

		handle.getDocumentContext().getRuntime().destroy();
	}
	
}
