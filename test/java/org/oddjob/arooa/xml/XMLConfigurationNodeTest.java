package org.oddjob.arooa.xml;

import java.net.URI;

import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.parsing.SimplePrefixMappings;

public class XMLConfigurationNodeTest extends XMLTestCase {

	class ParentContext extends MockArooaContext {
		
	}
	
	class OurParseContext extends MockArooaContext {
		
		PrefixMappings prefixMappings = new SimplePrefixMappings();
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return prefixMappings;
		}
	}
		
	public void testParse() throws Exception {

		// The elements
		
		ArooaElement parentElement = new ArooaElement(
				"fruit");
		
		MutableAttributes attrs1 = new MutableAttributes();
		attrs1.set("name", "cox");
		attrs1.set("colour", "red");
		ArooaElement appleElement = new ArooaElement(
				"apple", attrs1);
		
		ArooaElement pipsElement = new ArooaElement(
				"pips");

		String appleText = "Tastes very nice.";
		
		MutableAttributes attrs2 = new MutableAttributes();
		attrs2.set("name", "conference");
		attrs2.set("colour", "green");
		ArooaElement pearElement = new ArooaElement(
				"pear", attrs2);
		
		
		OurParseContext context = new OurParseContext();
		
		XMLConfigurationNode parentNode = new XMLConfigurationNode(
				parentElement);
		parentNode.setContext(context);
		
		XMLConfigurationNode appleNode = new XMLConfigurationNode(
				appleElement);
		appleNode.setContext(context);
		
		appleNode.addText(appleText);
		
		XMLConfigurationNode pipsNode = new XMLConfigurationNode(
				pipsElement);
		pipsNode.setContext(context);

		XMLConfigurationNode pearNode = new XMLConfigurationNode(
				pearElement);
		pearNode.setContext(context);
		
		appleNode.insertChild(pipsNode);
		
		parentNode.insertChild(appleNode);
		parentNode.insertChild(pearNode);
		
		XMLArooaParser parser = new XMLArooaParser();
		
		parser.parse(parentNode);
		
		String ls = System.getProperty("line.separator");
		
		String expected = "<fruit>" + ls +
				"    <apple name=\"cox\"" + ls +
				"           colour=\"red\">" + ls +
				"        <pips/><![CDATA[Tastes very nice.]]></apple>" + ls +
				"    <pear name=\"conference\"" + ls +
				"          colour=\"green\"/>" + ls +
				"</fruit>" + ls; 
		
		assertXMLEqual(expected, parser.getXml());
	}
	
	public void testParseNS() throws Exception {

		// The elements
		
		ArooaElement parentElement = new ArooaElement(
				new URI("http://www.rgordon.co.uk/arooa"), 
				"fruit");
		
		MutableAttributes attrs1 = new MutableAttributes();
		attrs1.set("name", "cox");
		attrs1.set("colour", "red");
		ArooaElement appleElement = new ArooaElement(
				new URI("http://www.rgordon.co.uk/fruit"),
				"apple", attrs1);
		
		ArooaElement pipsElement = new ArooaElement(
				new URI("http://www.rgordon.co.uk/fruit"), 
				"pips");

		String appleText = "Tastes very nice.";
		
		MutableAttributes attrs2 = new MutableAttributes();
		attrs2.set("name", "conference");
		attrs2.set("colour", "green");
		ArooaElement pearElement = new ArooaElement(
				new URI("http://www.rgordon.co.uk/fruit"), 
				"pear", attrs2);
		
		OurParseContext context = new OurParseContext();
		context.getPrefixMappings().put(
				"arooa",
				new URI("http://www.rgordon.co.uk/arooa"));
		context.getPrefixMappings().put(
				"fruit",
				new URI("http://www.rgordon.co.uk/fruit"));

		XMLConfigurationNode parentNode = new XMLConfigurationNode(
				parentElement);
		parentNode.setContext(context);
		
		XMLConfigurationNode appleNode = new XMLConfigurationNode(
				appleElement);
		appleNode.setContext(context);
		
		appleNode.addText(appleText);
		
		XMLConfigurationNode pipsNode = new XMLConfigurationNode(
				pipsElement);
		pipsNode.setContext(context);

		XMLConfigurationNode pearNode = new XMLConfigurationNode(
				pearElement);
		pearNode.setContext(context);

		appleNode.insertChild(pipsNode);
		
		parentNode.insertChild(appleNode);
		parentNode.insertChild(pearNode);
		
		XMLArooaParser parser = new XMLArooaParser();
		
		parser.parse(parentNode);
		
		String ls = System.getProperty("line.separator");
		
		String expected = 
			    "<arooa:fruit xmlns:arooa=\"http://www.rgordon.co.uk/arooa\"" + ls +
			    "             xmlns:fruit=\"http://www.rgordon.co.uk/fruit\">" + ls +
				"    <fruit:apple name=\"cox\"" + ls +
				"                 colour=\"red\">" + ls +
				"        <fruit:pips/><![CDATA[Tastes very nice.]]></fruit:apple>" + ls +
				"    <fruit:pear name=\"conference\"" + ls +
				"                colour=\"green\"/>" + ls +
				"</arooa:fruit>" + ls; 
		
		assertXMLEqual(expected, parser.getXml());
	}
	
	public void testText() {
		
		XMLConfigurationNode test1 = new XMLConfigurationNode(
				null);
		
		test1.addText("Test");
		
		assertEquals("Test", test1.getText());
		
		XMLConfigurationNode test2 = new XMLConfigurationNode(
				null);
		
		test2.addText(" a ");
		test2.addText(" b ");
		
		assertEquals("a  b", test2.getText().trim());
	}
}
