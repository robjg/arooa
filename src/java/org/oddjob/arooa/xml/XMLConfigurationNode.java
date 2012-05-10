package org.oddjob.arooa.xml;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.AbstractConfigurationNode;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.TextHandler;
import org.oddjob.arooa.runtime.ConfigurationNode;

public class XMLConfigurationNode extends AbstractConfigurationNode {

	private final ArooaElement element;

    /** Text appearing within the element. */
    private final TextHandler textHandler = new TextHandler();
    
    private ArooaContext context;
        
    /**
     * Constructor
     * 
     * @param element
     * @param prefixMappings
     */
	public XMLConfigurationNode(
			ArooaElement element) {
		this.element = element;
	}
	
    /**
     * Adds characters from #PCDATA areas to the wrapped element.
     *
     * @param data Text to add.
     *        Should not be <code>null</code>.
     */
    public void addText(String data) throws ArooaException {
    	textHandler.addText(data);
    }

    public String getText() {
    	return textHandler.getText();
    }
    	
	public ConfigurationHandle parse(ArooaContext parentContext) throws ArooaParseException {
		
		parentContext.getPrefixMappings().add(
				context.getPrefixMappings());
		
		final ArooaContext newContext = parentContext.getArooaHandler(
				).onStartElement(element, parentContext);

		if (textHandler.getText() != null) {
			newContext.getConfigurationNode().addText(textHandler.getText());
		}
		
		for (ConfigurationNode child: children()) {
			child.parse(newContext);			
		}
		
		int index = parentContext.getConfigurationNode().insertChild(
				newContext.getConfigurationNode());
		
		try {
			newContext.getRuntime().init();
		}
		catch (RuntimeException e) {
			parentContext.getConfigurationNode().removeChild(index);
			throw e;
		}
		
		return new ChainingConfigurationHandle(
				getContext(), parentContext, index);
	}
	
	public ArooaContext getContext() {
		return context;
	}

	public void setContext(ArooaContext context) {
		this.context = context;
	}
	
	@Override
	public String toString() {
		return "XMLConfigurationNode for " + element;
	}
}
