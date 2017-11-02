/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.deploy;
import org.junit.Before;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLConfiguration;

/**
 * 
 */
public class ArooaDescriptorElementsTest extends ArooaDescriptorTestBase {
	
	ArooaDescriptorFactory test;
	
   @Before
   public void setUp() throws ArooaParseException {
		
		ArooaConfiguration config = new XMLConfiguration(
				"Descriptor",
				ArooaDescriptorElementsTest.class.getResourceAsStream(
						"ArooaDescriptorElementsTest.xml"));
		
		StandardFragmentParser parser =
			new StandardFragmentParser(new ArooaDescriptorDescriptor());
		
		parser.parse(config);
		
		test = (ArooaDescriptorFactory) parser.getRoot();
	}
	
	@Override
	ArooaDescriptor getTest(ClassLoader loader) {
		return test.createDescriptor(loader);
	}
}
