package org.oddjob.arooa.design;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

import org.junit.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.deploy.NullArooaDescriptor;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

public class DesignInstanceContextTest {

	class OurDesignF implements DesignFactory {
		
		
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new OurDesign(element, parentContext);
		}
	}
	
	
	class OurDesign extends DesignValueBase {
		
		private final SimpleDesignProperty colour;
		
		private final SimpleTextProperty description;
		
		public OurDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, new SimpleArooaClass(Object.class), parentContext);
			
			colour = new SimpleDesignProperty(
					"colour", String.class, ArooaType.VALUE, this);
			
			description = new SimpleTextProperty(
					"description");
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] { colour, description };
		}
		
		public Form detail() {
			throw new RuntimeException("Unexpected.");
		}
	}
	
	String EOL = System.getProperty("line.separator");
	
   @Test
	public void testParseXML() throws ArooaParseException {
		
		DesignParser test = new DesignParser(
				new StandardArooaSession(new NullArooaDescriptor()),
				new OurDesignF());
		
		test.setExpectedDocumentElement(new ArooaElement("car"));
		
		String xml = 
			"<car>" + EOL +
			"    <colour>" +
			"		<value value='Red'/>" +
			"    </colour>A Shiny Red Car." + EOL +
			"</car>" + EOL;
		
		test.parse(new XMLConfiguration("TEST", xml));
		
		OurDesign design = (OurDesign) test.getDesign();
		
		assertEquals("A Shiny Red Car.\n", design.description.text());
	}
	
   @Test
	public void testPropertyWithAttributes() throws Exception {
		
		DesignParser test = new DesignParser(
				new StandardArooaSession(new NullArooaDescriptor()),
				new OurDesignF());
		
		test.setExpectedDocumentElement(new ArooaElement("car"));
		
		String xml = 
			"<car>" + EOL +
			"    <colour value=\"thisiswrong\"/>" + EOL +
			"</car>" + EOL;
		
		test.parse(new XMLConfiguration("TEST", xml));
		
		Unknown result = (Unknown) test.getDesign();

		assertThat(result.getXml(), isSimilarTo(xml));
	}
	
}
