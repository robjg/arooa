package org.oddjob.arooa.standard;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.annotations.ArooaElement;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.xml.XMLConfiguration;

public class StandardRuntimeTest extends Assert {

	public static class Component {
		
		String colour;
		
		@ArooaElement
		public void setColour(String colour) {
			this.colour = colour;
		}
	}
	
	
   @Test
	public void testManySave() throws ArooaParseException {

		Component root = new Component();

		StandardArooaParser parser = new StandardArooaParser(root);
		
		String xml = 
			"<component>" +
			"    <colour>" +
			"        <value value='red'/>" + 
			"    </colour>" +
			"</component>";
		
		ConfigurationHandle<ArooaContext> handle = parser.parse(
				new XMLConfiguration("TEST", xml));
				
		assertNull(root.colour);
		
		handle.getDocumentContext().getRuntime().configure();
		
		assertEquals("red", root.colour);

		handle.getDocumentContext().getRuntime().destroy();
		
		assertNull(root.colour);
	}
	
}
