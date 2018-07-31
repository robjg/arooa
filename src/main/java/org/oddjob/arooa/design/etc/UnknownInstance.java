/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.etc;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.Unknown;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.TextInput;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.AbstractConfigurationNode;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.runtime.AbstractRuntimeConfiguration;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.arooa.xml.XMLInterceptor;

/**
 *
 */
public class UnknownInstance 
implements DesignInstance,
		Unknown {

	private String xml = "";
	
	private final ArooaContext arooaContext;

	private final ArooaElement element;

	public UnknownInstance(ArooaElement element, ArooaContext parentContext) {
		this.element = element;
		arooaContext = new UnknownContext(parentContext);
	}
	
	public ArooaElement element() {
		return element;
	}
		
	public String toString() {
		return "XML";
	}

	public void setXml(String xml) {
		if (xml == null) {
			throw new NullPointerException("The XML.");
		}
		this.xml = xml;
	}

	public String getXml() {
		return this.xml;
	}
	
	public Form detail() {
		return new TextInput("XML", 
				new TextInput.TextSource() {
			public String getText() {
				return xml;
			}
			public void setText(String text) {
				xml = text;
			}
		});
	}
	
	
	public ArooaContext getArooaContext() {
		return arooaContext;
	}
	
	class UnknownContext implements ArooaContext {

		ArooaContext parentContext;
		
		ConfigurationNode configurationNode = 
			new AbstractConfigurationNode() {
				public void addText(String text) {
					firstElementContext.getConfigurationNode().addText(text);
				}
				
				public ArooaContext getContext() {
					return UnknownContext.this;
				}
				
				public ConfigurationHandle parse(ArooaContext parentContext)
				throws ArooaParseException {
					
					if (xml.trim().length() > 0) {
						XMLConfiguration config = new XMLConfiguration(
								UnknownInstance.this.toString(), xml);
						return config.parse(parentContext);						
					}
					else {
						return null;
					}
				}
				
		};

		RuntimeConfiguration runtime = 
			new AbstractRuntimeConfiguration() {
			
			public ArooaClass getClassIdentifier() {
				return new SimpleArooaClass(Object.class);
			}
			
			public void init() throws ArooaConfigurationException {
				firstElementContext.getRuntime().init();
				
				fireBeforeInit();
				
				RuntimeConfiguration parentRuntime = parentContext.getRuntime(); 

				// check it's not the root
				if (parentRuntime != null) {
					
					int index = parentContext.getConfigurationNode().indexOf(
							arooaContext.getConfigurationNode());
					
					if (index < 0) {
						throw new IllegalStateException(
								"Configuration node not added to parent.");
					}
				
					parentRuntime.setIndexedProperty(null, index, UnknownInstance.this);
				}
				
				fireAfterInit();
			}
			public void configure() {
				fireBeforeConfigure();
				fireAfterConfigure();
			}
			public void destroy() throws ArooaConfigurationException {
				fireBeforeDestroy();
				
				RuntimeConfiguration parentRuntime = parentContext.getRuntime(); 

				// check it's not the root
				if (parentRuntime != null) {
					
					int index = parentContext.getConfigurationNode().indexOf(
							arooaContext.getConfigurationNode());
					
					if (index < 0) {
						throw new IllegalStateException(
								"Configuration node not added to parent.");
					}
					
					parentContext.getRuntime().setIndexedProperty(null, index, null);
				}
				
				fireAfterDestroy();
			}				
			
			public void setIndexedProperty(String name, int index,
					Object value) throws ArooaException {
				throw new UnsupportedOperationException();
			}				
			public void setMappedProperty(String name, String key,
					Object value) throws ArooaException {
				throw new UnsupportedOperationException();
			}
			public void setProperty(String name, Object value)
					throws ArooaException {
				// Value null when XMLInterceptor Runtime destroyed.
				if (value == null) {
					setXml("Destroyed - You Should Never See This!");
				}
				else {
					setXml((String) value);
				}
			}
		};

		private ArooaContext firstElementContext;
				
		public UnknownContext(ArooaContext parentContext) {
			this.parentContext = parentContext;
			
			ArooaContext interceptContext = new XMLInterceptor("xml").intercept(this);
			
			firstElementContext = interceptContext.getArooaHandler().onStartElement(
					element, interceptContext);
		}
		
		public ArooaType getArooaType() {
			return null;
		}
		
		public ArooaContext getParent() {
			return parentContext;
		}
		
		public ArooaSession getSession() {
			return parentContext.getSession();
		}

		public RuntimeConfiguration getRuntime() {
			return runtime;
		}
			
		public ArooaHandler getArooaHandler() {
			return firstElementContext.getArooaHandler();
		}
		
		public ConfigurationNode getConfigurationNode() {
			return configurationNode;
		}
		
		public PrefixMappings getPrefixMappings() {
			return parentContext.getPrefixMappings();
		}
	}
	
}
