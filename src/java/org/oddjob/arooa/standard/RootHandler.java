package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.life.ArooaElementException;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;

/**
 * The first handler to be called. This will receive
 * an onStartElement with the document root as the 
 * element. 
 * <p>
 * If the document tag is specified this will 
 * verify the name of the element
 * root against the document tag.
 * <p>
 * The startHandler is provided as the handler for
 * dealing with this document element. Thus the startHandler
 * can then process the document element in it's onStartElement
 * method as per the typical pattern of an ArooaHandler.
 *
 * @see ArooaHandler.
 * @author Rob Gordon.
 */
public class RootHandler implements ArooaHandler {

	private final ArooaElement documentTag;

	/** The start handler */
	private final ElementAction<? extends InstanceRuntime> startAction;
	/**
	
	/**
	 * Constructor.
	 *
	 * @param handler The handler to use for the top level element.
	 */
	public RootHandler(ElementAction<? extends InstanceRuntime> startHandler) {
		this.documentTag = null;	
		this.startAction = startHandler;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param documentTag The document tag which will be validated
	 * against the top level element if not null.
	 * @param handler The handler to use for the top level element.
	 */
	public RootHandler(ArooaElement documentTag, ElementAction<? extends InstanceRuntime> startHandler) {
		this.documentTag = documentTag;	
		this.startAction = startHandler;
	}
	
    /**
     * Handle the top level element.
     *
     */
	public ArooaContext onStartElement(ArooaElement element, ArooaContext parentContext) 
	throws ArooaConfigurationException {
        // check the root tag is what we expect
        if (documentTag != null && !element.equals(documentTag)) {
            throw new ArooaElementException(element, 
            		"Unexpected document element, expected \"" + documentTag + "\"");
        } 
        
        final InstanceRuntime runtime = startAction.onElement(element, parentContext);
        
		InstanceConfigurationNode node = new InstanceConfigurationNode(
				element, runtime);
		
		ArooaContext ourContext = new StandardArooaContext(
				parentContext.getArooaType(), runtime, node, parentContext);				
		
		runtime.setContext(ourContext);

		return runtime.getContext();
    }
	
}

 