package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;

/**
 * Create an {@link ArooaConfiguration} out of a single element.
 * Used for creating new configurations.
 * 
 * @author rob
 *
 */
public class ElementConfiguration implements ArooaConfiguration {

	/** The element. */
	private final ArooaElement element;
	
	
	public ElementConfiguration(ArooaElement element) {
		this.element = element;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.ArooaConfiguration#parse(org.oddjob.arooa.parsing.ArooaContext)
	 */
	public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
			throws ArooaParseException {
		
		QTagConfiguration qTagConfiguration = new QTagConfiguration(
				new QTag(element, parentContext));
		
		return 	qTagConfiguration.parse(parentContext);
	}
	
}
