package org.oddjob.arooa.types;

import java.io.Serializable;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.etc.UnknownInstance;
import org.oddjob.arooa.life.ArooaContextAware;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ChildCatcher;

/**
 * @oddjob.description A type that converts it's XML contents into
 * a String.
 * 
 * @oddjob.example
 * 
 * Capture XML in a variable.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/XMLTypeExample.xml}
 * 
 * @author Rob Gordon.
 */
public class XMLType implements ArooaContextAware, ArooaValue, Serializable {
	private static final long serialVersionUID = 20081118;
	
	public static final ArooaElement ELEMENT = new ArooaElement("xml"); 
		
	private ArooaContext arooaContext;
	
	/**
	 * @oddjob.property
	 * @oddjob.description This is only used internally. It can't
	 * be set via configuration because all contents are converted
	 * into text XML.
	 * @oddjob.required Irrelevant.
	 */
	private String xml;

	public static class Conversions implements ConversionProvider {
		
		public void registerWith(ConversionRegistry registry) {

			registry.register(XMLType.class, String.class, 
					new Convertlet<XMLType, String>() {
				public String convert(XMLType from)
						throws ConvertletException {
	    			return from.xml;
				}
			});
			
			registry.register(XMLType.class, ArooaConfiguration.class, 
					new Convertlet<XMLType, ArooaConfiguration>() {
				public ArooaConfiguration convert(XMLType from)
						throws ConvertletException {
					
					ArooaContext childContext = 
						new ChildCatcher(from.arooaContext, 0).getChild();
					
					if (childContext == null) {
						return null;
					}
					
					return childContext.getConfigurationNode();					
				}
			});
		}
	}
	
	@Override
	public void setArooaContext(ArooaContext context) {
		this.arooaContext = context;
	}
	
	public void setXml(String xml) {
		this.xml = xml;
	}
	
    public String toString() {
    	return "Embedded XML";
    }
        
	public static class XMLDesignFactory implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, 
				ArooaContext parentContext) {
			
			if (parentContext.getArooaType() == ArooaType.COMPONENT) {
				throw new IllegalArgumentException("Can't be a component.");
			}
			else {
				return new UnknownInstance(element, parentContext);		
			}
		}
	};

}
