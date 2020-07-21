package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.runtime.ConfigurationNode;

import java.util.function.Supplier;

/**
 * An {@link ConfigurationNode} for standard parsing.
 */
abstract class StandardConfigurationNode extends AbstractConfigurationNode {

	private final Supplier<ArooaElement> element;

    /**
     * Constructor
     * 
     * @param element The element this is a configuration node for.
     */
	public StandardConfigurationNode(
            Supplier<ArooaElement> element) {
		this.element = element;
	}
	
    abstract public String getText();

	@Override
	public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
	throws ArooaParseException {
		
		parentContext.getPrefixMappings().add(
				getContext().getPrefixMappings());

		ParseHandle<P> handle;
		try {
			handle = parentContext.getElementHandler(
					).onStartElement(element.get(), parentContext);
		} catch (ArooaConfigurationException e) {
    		throw new ArooaParseException("Failed parsing configuration.", 
    				new Location("Unknown", 0, 0), e);
		}

		P newContext = handle.getContext();

		if (getText() != null) {
			handle.addText(getText());
		}
		
		for (ConfigurationNode<ArooaContext> child: children()) {
			child.parse(newContext);			
		}

		int index;
		try {
			index = handle.init();
    	} catch (ArooaConfigurationException e) {
    		throw new ArooaParseException("Failed initialising.",
    				new Location("Unknown", 0, 0), e);
    	}

		return new ChainingConfigurationHandle(getContext(), 
				parentContext, index);	
	}

}
