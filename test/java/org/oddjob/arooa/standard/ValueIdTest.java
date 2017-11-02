package org.oddjob.arooa.standard;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ValueIdTest extends Assert {
	
	public static class Root {
		
		public void setValue(Object value) {
		}
	}
	
   @Test
	public void testSimpleValueId() throws ArooaParseException {
		
		String xml = 
			"<root>" +
			" <value>" +
			"  <value id='val' value='apples'/>" +
			" </value>" +
			"</root>";
	
		Root root  = new Root();
		
		StandardArooaParser parser = new StandardArooaParser(root);

		try {
			parser.parse(new XMLConfiguration("XML", xml));
			
			fail("Should fail");
		}
		catch (ArooaParseException e) {

			ArooaPropertyException cause = (ArooaPropertyException) e.getCause();
			
			assertEquals("id", cause.getProperty());
		}
	}
	
	
}
