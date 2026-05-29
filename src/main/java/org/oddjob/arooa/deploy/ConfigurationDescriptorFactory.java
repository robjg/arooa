package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.standard.StandardFragmentParser;

/**
 * A very simple {@link ArooaDescriptorFactory} that loads a descriptor
 * definition from an {@link ArooaConfiguration} such an XML file.
 * 
 * @author rob
 *
 */
public class ConfigurationDescriptorFactory implements ArooaDescriptorFactory{

	/** We only want to create this once. */
	private static final StandardFragmentParser parser =
			new StandardFragmentParser(new ArooaDescriptorDescriptor());

	/** The configuration. */
	private final ArooaConfiguration config;
	
	public ConfigurationDescriptorFactory(ArooaConfiguration config) {
		this.config = config;
	}
	
	@Override
	public ArooaDescriptor createDescriptor(ClassLoader classLoader) {

		try {
			parser.parse(config);
		} catch (ArooaParseException e) {
			throw new RuntimeException(
					"Failed creating descriptor factory from " + 
					config, e);
		}

		ArooaDescriptorFactory factory = 
			(ArooaDescriptorFactory) parser.getRoot();

		return factory.createDescriptor(classLoader);
	}
}
