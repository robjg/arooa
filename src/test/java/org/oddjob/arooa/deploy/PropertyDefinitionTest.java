package org.oddjob.arooa.deploy;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class PropertyDefinitionTest extends Assert {

   @Test
	public void testXMLConfiguration() throws ArooaParseException {
		
				
		ArooaSession session = new StandardArooaSession(
				new ArooaDescriptorDescriptor());
		
		StandardFragmentParser parser = new StandardFragmentParser(
				session);
		
		ArooaConfiguration config = 
			new XMLConfiguration("TEST",
					"<arooa:property xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'" +
					" name='myProp' type='ATTRIBUTE' " +
					" auto='true' flavour='red'/>");
		
		parser.parse(config);
		
		PropertyDefinition test = (PropertyDefinition) parser.getRoot();
		
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				test.getConfiguredHow());
		assertEquals("red", test.getFlavour());
		
		assertEquals(new Boolean(true), test.getAuto());
	}
	
		
	
}
