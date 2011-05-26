package org.oddjob.arooa.parsing;

import java.net.URI;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;

public class QTagConfiguration implements ArooaConfiguration {

	private final QTag tag; 
	
	public QTagConfiguration(QTag tag) {
		this.tag = tag;
	}
	
	public ConfigurationHandle parse(final ArooaContext parentContext) 
	throws ArooaParseException {

		ArooaElement element = tag.getElement();
	
		URI uri = element.getUri();
		
		if (uri != null) {
			
			// Add prefix.
			parentContext.getPrefixMappings().put(
					tag.getPrefix(), uri); 
		}
		
		ArooaContext newContext = parentContext.getArooaHandler().onStartElement(element, parentContext);
		
		parentContext.getConfigurationNode().insertChild(
				newContext.getConfigurationNode());

		final int index = parentContext.getConfigurationNode().indexOf(
				newContext.getConfigurationNode());

		try {
			newContext.getRuntime().init();
    	} catch (ArooaConfigurationException e) {
    		parentContext.getConfigurationNode().removeChild(index);
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
			
			public ArooaContext getDocumentContext() {
				ChildCatcher childCatcher = new ChildCatcher(
						parentContext, index);
				
				return childCatcher.getChild();
			}
		};
	}
	
}
