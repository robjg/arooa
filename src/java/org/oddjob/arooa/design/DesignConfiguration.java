package org.oddjob.arooa.design;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaConstants;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.Location;

class DesignConfiguration implements ArooaConfiguration {

	private final DesignInstanceBase design;
	
	public DesignConfiguration(DesignInstanceBase design) {
		this.design = design;
	}
	
    public ConfigurationHandle parse(ArooaContext parentContext) 
    throws ArooaParseException {
	
    	ArooaElement element = new ArooaElement(
    			design.element().getUri(), 
    			design.element().getTag());

		parentContext.getPrefixMappings().add(
				design.getArooaContext().getPrefixMappings());
		
    	ArooaHandler handler = parentContext.getArooaHandler();

    	if (design instanceof DesignComponent) {
        	String id = ((DesignComponent) design).getId();
        	if (id != null && id.length() > 0) {
    			element = element.addAttribute(
    					ArooaConstants.ID_PROPERTY, id);
        	}
    	}
    	
    	for (DesignProperty child: design.children()) {
    		if (child instanceof DesignAttributeProperty) {
    			
        		DesignAttributeProperty attributeProperty = 
        			(DesignAttributeProperty) child;
        		    		
        		if (attributeProperty.attribute() != null && 
        				attributeProperty.attribute().length() > 0) {
        			element = element.addAttribute(
        					child.property(), attributeProperty.attribute());
        		}
    		}
    		else if (child instanceof DesignTextProperty) {
    			continue;
    		}
    		else if (child instanceof DesignElementProperty) {
    			continue;
    		}
    		else {
    			throw new IllegalStateException("Unsupported property " + child);
    		}
    	}

    	ArooaContext nextContext = null;
    	
    	try {
    		nextContext = handler.onStartElement(
    			element, parentContext);
    	}
    	catch (ArooaConfigurationException e) {
    		throw new ArooaParseException("Failed parsing design.", 
    				new Location(design.toString(), 0, 0), e);    		
    	}
    	
    	for (DesignProperty child: design.children()) {
    		
    		if (child instanceof DesignAttributeProperty) {
    			continue;
    		}
    		else if (child instanceof DesignTextProperty) {

    			DesignTextProperty textProperty = 
    				(DesignTextProperty) child;

    			if (textProperty.text() != null) {
    				nextContext.getConfigurationNode().addText(
    						textProperty.text());
    			}
    		}
    		else if (child instanceof DesignElementProperty) {    			
        		parse(nextContext, (DesignElementProperty) child);
    		} else {
        		throw new IllegalStateException("Unsupported property " + child);
        	}
    	}

    	int index = parentContext.getConfigurationNode().insertChild(
    			nextContext.getConfigurationNode());    		

    	try {
    		nextContext.getRuntime().init();
    	} catch (Exception e) {
    		try {
    			nextContext.getRuntime().destroy();
    		} catch (Exception e2) {
    			throw new RuntimeException(
    					"Failed rolling back design change.", e);
    		}
    		parentContext.getConfigurationNode().removeChild(index);
    		throw new ArooaParseException("Failed parsing design.", 
    				new Location(design.toString(), 0, 0), e);
    	}

		return new ContextConfigurationHandle(nextContext);
 	}
    
    class ContextConfigurationHandle implements ConfigurationHandle {
    	
    	private final ArooaContext nextContext;
    	
    	public ContextConfigurationHandle(ArooaContext nextContext) {
    		this.nextContext = nextContext;
		}
    	
		public void save() throws ArooaParseException {
			throw new UnsupportedOperationException(
					"Not Implemented... should it be?");
		}
		
		public ArooaContext getDocumentContext() {
			// Is it possible that this will have changed?
			// Do we need to use a ChildCatcher?
			return nextContext;
		}
    }
    
	
    private ConfigurationHandle parse(ArooaContext parentContext, DesignElementProperty designProperty) throws ArooaParseException {

    	return designProperty.getArooaContext().getConfigurationNode().parse(parentContext);
 	}	
}
