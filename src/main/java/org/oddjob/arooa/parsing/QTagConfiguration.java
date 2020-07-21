package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;

import java.net.URI;

public class QTagConfiguration implements ArooaConfiguration {

	private final QTag tag; 
	
	public QTagConfiguration(QTag tag) {
		this.tag = tag;
	}

	@Override
	public <P extends ParseContext<P>> ConfigurationHandle<P> parse(final P parentContext)
	throws ArooaParseException {

		ArooaElement element = tag.getElement();
	
		URI uri = element.getUri();
		
		if (uri != null) {
			// Add prefix.
			parentContext.getPrefixMappings().put(
					tag.getPrefix(), uri); 
		}

		ParseHandle<P> handle = parentContext.getElementHandler().onStartElement(element, parentContext);

		int index;

		try {
			index = handle.init();
		} catch (ArooaConfigurationException e) {
    		throw new ArooaParseException(
    				"Failed initialising new node.", 
    				new Location("QTag " + tag.toString(), 0, 0),
    				e);
    	}

		return new ConfigurationHandle() {
			public void save() throws ArooaParseException {
				// There's nowhere to save to.
				
				// Should this throw UnsupportedOperationException?
			}

			public P getDocumentContext() {
				ChildCatcher<P> childCatcher = new ChildCatcher<>(
						parentContext, index);
				
				return childCatcher.getChild();
			}
		};
	}
	
}
