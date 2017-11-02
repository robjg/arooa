package org.oddjob.arooa.xml;

import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import org.junit.Test;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaContext;

public class XMLArooaParserTest {

	static final String EOL = System.getProperty("line.separator");
	
   @Test
	public void testRoundTrip() throws Exception {

		String xml = "<comp>" +
						"<a><b/></a>" +
						"<x><y/></x>" +
						"</comp>";
				
		XMLArooaParser parser = new XMLArooaParser();
		ConfigurationHandle handle = parser.parse(new XMLConfiguration("Test", xml));
		
		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + EOL +
				"<comp>" + EOL +
				"    <a>" + EOL + 
				"        <b/>" + EOL + 
				"    </a>" + EOL + 
				"    <x>" + EOL + 
				"        <y/>" + EOL + 
				"    </x>" + EOL +
				"</comp>" + EOL;
		
		assertThat(parser.getXml(), isIdenticalTo(expected));
		
		ArooaContext docContext = handle.getDocumentContext();
		
		parser.parse(docContext.getConfigurationNode());
		
		assertThat(parser.getXml(), isIdenticalTo(expected));
	}

   @Test
	public void testRoundTripNS() throws Exception {

		String xml = "<comp xmlns='http://www.rgordon.co.uk/arooa'" +
					"		xmlns:fruit='http://www.rgordon.co.uk/fruit'>" +
					"<a><fruit:b/></a>" +
					"<x><fruit:y/></x>" +
					"</comp>";
				
		XMLArooaParser parser = new XMLArooaParser();
		ConfigurationHandle handle = parser.parse(new XMLConfiguration("Test", xml));
		
		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + EOL +				
				"<comp xmlns=\"http://www.rgordon.co.uk/arooa\"" + EOL +
				"      xmlns:fruit=\"http://www.rgordon.co.uk/fruit\">" + EOL +
				"    <a>" + EOL + 
				"        <fruit:b/>" + EOL + 
				"    </a>" + EOL + 
				"    <x>" + EOL + 
				"        <fruit:y/>" + EOL + 
				"    </x>" + EOL +
				"</comp>" + EOL;
		
		assertThat(parser.getXml(), isIdenticalTo(expected));
		
		ArooaContext docContext = handle.getDocumentContext();
		
		parser.parse(docContext.getConfigurationNode());
		
		assertThat(parser.getXml(), isIdenticalTo(expected));
	}
}
