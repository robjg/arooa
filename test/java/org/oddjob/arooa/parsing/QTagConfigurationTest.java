package org.oddjob.arooa.parsing;

import java.net.URI;
import java.net.URISyntaxException;

import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class QTagConfigurationTest extends XMLTestCase {

	public void testParse() throws Exception {
		
		QTagConfiguration test = new QTagConfiguration(
				new QTag("fruit", 
						new ArooaElement(new URI("http://fruit"), "apple")));
		
		XMLArooaParser parser = new XMLArooaParser();
		
		parser.parse(test);
		
		String expected = "<fruit:apple xmlns:fruit=\"http://fruit\"/>" + System.getProperty("line.separator");

		assertXMLEqual(expected,
				parser.getXml());
	}
	
	/**
	 * Try to simulate what happens when a new node is added to an existing configuration
	 * and then saved.
	 * 
	 * @throws ArooaParseException
	 * @throws URISyntaxException
	 */
	public void testParse1() throws Exception {
		
		Object root = new Object();
		
		XMLConfiguration config = new XMLConfiguration("TEST", "<veg/>");
	
		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(config);
		
		QTagConfiguration test = new QTagConfiguration(
				new QTag("fruit", 
						new ArooaElement(new URI("http://fruit"), "apple")));

		CutAndPasteSupport.replace(handle.getDocumentContext().getParent(), handle.getDocumentContext(), test);
		
		handle.save();
		
		String expected = "<fruit:apple xmlns:fruit=\"http://fruit\"/>" + System.getProperty("line.separator");

		assertXMLEqual(expected,
				config.getSavedXml());
	}
}
