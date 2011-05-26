package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.AbstractConfigurationNode;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.Location;
import org.oddjob.arooa.runtime.ConfigurationNode;

abstract class StandardConfigurationNode extends AbstractConfigurationNode {

	private final ArooaElement element;

    /**
     * Constructor
     * 
     * @param element
     * @param prefixMappings
     */
	public StandardConfigurationNode(
			ArooaElement element) {
		this.element = element;
	}
	
    abstract public String getText();
	
	public ConfigurationHandle parse(ArooaContext parentContext) 
	throws ArooaParseException {
		
		parentContext.getPrefixMappings().add(
				getContext().getPrefixMappings());
		
		ArooaContext newContext;
		try {
			newContext = parentContext.getArooaHandler(
					).onStartElement(element, parentContext);
		} catch (ArooaConfigurationException e) {
    		throw new ArooaParseException("Failed parsing configuration.", 
    				new Location("Unknown", 0, 0), e);
		}

		if (getText() != null) {
			newContext.getConfigurationNode().addText(getText().toString());
		}
		
		for (ConfigurationNode child: children()) {
			child.parse(newContext);			
		}
		
		int index = parentContext.getConfigurationNode().insertChild(
				newContext.getConfigurationNode());
 
		try {
			newContext.getRuntime().init();
    	} catch (ArooaConfigurationException e) {
    		parentContext.getConfigurationNode().removeChild(index);
    		throw new ArooaParseException("Failed initialising.", 
    				new Location("Unknown", 0, 0), e);
    	}

		return new ChainingConfigurationHandle(getContext(), 
				parentContext, index);	
	}

}
