package org.oddjob.arooa.types;

import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.annotations.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ImportTypeTest2 extends XMLTestCase {

	public static class StringCapture {
		String stuff;
		
		@ArooaElement
		public void setStuff(String stuff) {
			this.stuff = stuff;
		}
	}
	
	String EOL = System.getProperty("line.separator");
	
	public void testImportXml() throws Exception {
		
		StringCapture root = new StringCapture();
		
		String xml = 
			"<whatever>" +
			" <stuff>" +
			"  <import xml='${xml}'/>" +
			" </stuff>" +
			"</whatever>";
		
		StandardArooaParser parser = new StandardArooaParser(root);

		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		String moreXML = 
			"<xml>" +
			" <something/>" +
			"</xml>";
		
		session.getBeanRegistry().register("xml", moreXML);
		session.getComponentPool().configure(root);
		
		String expected = 
			"<something/>";
		
		assertXMLEqual(expected, root.stuff); 
	}
	
	public void testImportRuntimeProperty() throws Exception {
		
		StringCapture root = new StringCapture();
		
		String xml = 
			"<whatever>" +
			" <stuff>" +
			"  <import xml='${xml}'/>" +
			" </stuff>" +
			"</whatever>";
		
		StandardArooaParser parser = new StandardArooaParser(root);

		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		String moreXML = 
			"<value value='${fruit}'/>";
		
		session.getBeanRegistry().register("xml", moreXML);
		session.getBeanRegistry().register("fruit", "apple");
		session.getComponentPool().configure(root);
		
		String expected = "apple";
		
		assertEquals(expected, root.stuff); 
	}
}
