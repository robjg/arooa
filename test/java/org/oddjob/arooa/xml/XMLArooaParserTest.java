package org.oddjob.arooa.xml;

import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaContext;

public class XMLArooaParserTest extends XMLTestCase {

	public void testRoundTrip() throws Exception {

		String xml = "<comp>" +
						"<a><b/></a>" +
						"<x><y/></x>" +
						"</comp>";
				
		XMLArooaParser parser = new XMLArooaParser();
		ConfigurationHandle handle = parser.parse(new XMLConfiguration("Test", xml));
		
		String ls = System.getProperty("line.separator");
		
		String expected = 
				"<comp>" + ls +
				"    <a>" + ls + 
				"        <b/>" + ls + 
				"    </a>" + ls + 
				"    <x>" + ls + 
				"        <y/>" + ls + 
				"    </x>" + ls +
				"</comp>" + ls;
		
		assertXMLEqual(expected, parser.getXml());
		
		ArooaContext docContext = handle.getDocumentContext();
		
		parser.parse(docContext.getConfigurationNode());
		
		assertXMLEqual(expected, parser.getXml());
	}

	public void testRoundTripNS() throws Exception {

		String xml = "<comp xmlns='http://www.rgordon.co.uk/arooa'" +
					"		xmlns:fruit='http://www.rgordon.co.uk/fruit'>" +
					"<a><fruit:b/></a>" +
					"<x><fruit:y/></x>" +
					"</comp>";
				
		XMLArooaParser parser = new XMLArooaParser();
		ConfigurationHandle handle = parser.parse(new XMLConfiguration("Test", xml));
		
		String ls = System.getProperty("line.separator");
		
		String expected = 
				"<comp xmlns=\"http://www.rgordon.co.uk/arooa\"" + ls +
				"      xmlns:fruit=\"http://www.rgordon.co.uk/fruit\">" + ls +
				"    <a>" + ls + 
				"        <fruit:b/>" + ls + 
				"    </a>" + ls + 
				"    <x>" + ls + 
				"        <fruit:y/>" + ls + 
				"    </x>" + ls +
				"</comp>" + ls;
		
		assertXMLEqual(expected, parser.getXml());
		
		ArooaContext docContext = handle.getDocumentContext();
		
		parser.parse(docContext.getConfigurationNode());
		
		assertXMLEqual(expected, parser.getXml());
	}
}
