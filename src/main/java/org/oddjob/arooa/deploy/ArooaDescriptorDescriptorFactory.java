package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLConfiguration;

/**
 * An {@link ArooaDescriptor} for a {@link ArooaConfiguration}
 * that provides and {@link ArooaDescriptorBean}.
 *  
 * @author rob
 *
 */
public class ArooaDescriptorDescriptorFactory implements ArooaDescriptorFactory {


	public ArooaDescriptor createDescriptor(ClassLoader classLoader) {
		
		XMLConfiguration config = new XMLConfiguration(
				"descriptor.xml",
				getClass().getResourceAsStream("descriptor.xml"));

		StandardFragmentParser parser = new StandardFragmentParser();
	
		try {
			parser.parse(config);
		} 
		catch (ArooaParseException e) {
			throw new RuntimeException(e);
		}
		
		ArooaDescriptorFactory descriptorFactory = 
			(ArooaDescriptorFactory) parser.getRoot();
		
		return descriptorFactory.createDescriptor(classLoader);
	}
	
}
