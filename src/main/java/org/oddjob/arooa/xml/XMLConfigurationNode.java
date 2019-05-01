package org.oddjob.arooa.xml;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.AbstractConfigurationNode;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.TextHandler;
import org.oddjob.arooa.runtime.ConfigurationNode;

import java.util.Objects;
import java.util.function.Supplier;

public class XMLConfigurationNode extends AbstractConfigurationNode {

	/** The element */
	private final ArooaElement element;

    /** Text appearing within the element. */
    private final TextHandler textHandler = new TextHandler();

    /** The context that owns this configuration node. It can't be set in the constructor
	 * because there is a chicken and egg situation between this and the context. It
	 * is set only once after the context has been created with this */
    private ArooaContext context;
        
    /**
     * Constructor
     * 
     * @param element
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
    	
	public ConfigurationHandle parse(ArooaContext parseParentContext) throws ArooaParseException {
		
		parseParentContext.getPrefixMappings().add(
				context.getPrefixMappings());
		
		final ArooaContext newContext = parseParentContext.getArooaHandler(
				).onStartElement(element, parseParentContext);

		if (textHandler.getText() != null) {
			newContext.getConfigurationNode().addText(textHandler.getText());
		}
		
		for (ConfigurationNode child: children()) {
			child.parse(newContext);			
		}
		
		int index = parseParentContext.getConfigurationNode().insertChild(
				newContext.getConfigurationNode());
		
		try {
			newContext.getRuntime().init();
		}
		catch (RuntimeException e) {
			parseParentContext.getConfigurationNode().removeChild(index);
			throw e;
		}
		
		return new ChainingConfigurationHandle(
				getContext(), parseParentContext, index);
	}
	
	public ArooaContext getContext() {
		return context;
	}

	public void setContext(ArooaContext context) {
		Objects.requireNonNull(context);
		if (this.context != null) {
			throw new IllegalStateException("Can't change context once set.");
		}
		this.context = context;
	}
	
	@Override
	public String toString() {
		return "XMLConfigurationNode for " + element;
	}
}
