package org.oddjob.arooa.design;

import org.oddjob.arooa.*;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListenerAdapter;
import org.oddjob.arooa.xml.XmlHandler2;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An {@link ArooaContext} for a {@link ParsableDesignInstance}
 * 
 * @author rob
 *
 */
public class DesignInstanceContext implements ArooaContext {

//	private static final Logger logger = LoggerFactory.getLogger(DesignInstanceContext.class);
	
	private final ArooaContext parent;
	
	private final ParsableDesignInstance instance;

	private final ArooaClass classIdentifier;

	private final TextHandler textHandler = new TextHandler();

	private final ConfigurationNode<ArooaContext> configurationNode
			= new DesignConfigurationNode() {

		@Override
		public ArooaContext getContext() {
			return DesignInstanceContext.this;
		}

		@Override
		public void addText(String text) {
			textHandler.addText(text);
		}

		@Override
		public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
				throws ArooaParseException {
			return new DesignConfiguration(instance).parse(parentContext);
		}
		
	};

	private final RuntimeConfiguration runtime = new DesignRuntime() {

		@Override
		public void init() 
		throws ArooaConfigurationException {
			fireBeforeInit();
			
			if (textHandler.length() > 0) {
				DesignTextProperty textProperty = getTextProperty();
				if (textProperty == null) {
					throw new ArooaException("No Text Property for [" + 
							textHandler.getText() + "]");
				}
				else {
					textProperty.text(textHandler.getText());
				}
			}

			RuntimeConfiguration parentRuntime = parent.getRuntime();
			
			// check it's not the root
			if (parentRuntime != null) {
				
				int index = parent.getConfigurationNode().indexOf(
						configurationNode);
			
				if (index < 0) {
					throw new IllegalStateException(
							"Configuration node not added to parent.");
				}
				
					parentRuntime.setIndexedProperty(null, index, instance);
			}
			
			fireAfterInit();
		}
		
		@Override
		public void destroy() 
		throws ArooaConfigurationException {
			fireBeforeDestroy();
			
			RuntimeConfiguration parentRuntime = parent.getRuntime();
			
			// check it's not the root
			if (parentRuntime != null) {
				
				int index = parent.getConfigurationNode().indexOf(
						configurationNode);

				if (index < 0) {
					throw new IllegalStateException(
							"Configuration node not added to parent.");
				}

				parentRuntime.setIndexedProperty(null, index, null);
			}

			fireAfterDestroy();
		}
		
		public void setProperty(String name, Object value) throws ArooaException {
			throw new UnsupportedOperationException("Use setIndexedProperty for Designs.");
		}
		
		public void setIndexedProperty(String name, int index, Object value)
				throws ArooaException {
			// Ignore this because the properties are fixed and created by the handler.
		}
		
		public void setMappedProperty(String name, String key, Object value)
				throws ArooaException {
			throw new UnsupportedOperationException("Use setIndexedProperty for Designs.");
		}
		
		@Override
		public ArooaClass getClassIdentifier() {
			return classIdentifier;
		}
	};
	
	private Map<String, ArooaContext> propertyContexts;
	
	/**
	 * Constructor.
	 * 
	 * @param instance
	 * @param classIdentifier
	 * @param parent
	 */
	public DesignInstanceContext(
			ParsableDesignInstance instance, 
			ArooaClass classIdentifier,
			ArooaContext parent) {

		Objects.requireNonNull(instance,"No Design.");
		Objects.requireNonNull(classIdentifier, "No class identifier.");
		Objects.requireNonNull(parent, "No parent context.");

		this.instance = instance;
		this.parent = parent;
		this.classIdentifier = classIdentifier;
	}

	public ArooaType getArooaType() {
		return parent.getArooaType();
	}
	
	public ArooaContext getParent() {
		return parent;
	}
	
	public RuntimeConfiguration getRuntime() {
		return runtime;
	}
	
	public PrefixMappings getPrefixMappings() {
		return parent.getPrefixMappings();
	}
	
	public ArooaSession getSession() {
		return parent.getSession();
	}
	
	public ConfigurationNode<ArooaContext> getConfigurationNode() {
		return configurationNode;
	}
	
	public ArooaHandler getArooaHandler() {
		return (element, parentContext) -> {

			final int[] error = new int[] { 0 };

			if (element.getAttributes().getAttributeNames().length > 0) {
				error[0] = 1;
			}

			ArooaContext propertyContext = getPropertyContext(element.getTag());

			if (propertyContext == null) {
				error[0] = 2;
			}

			if (error[0] > 0) {
				final XmlHandler2 handler = new XmlHandler2();

				ArooaContext xmlContext = handler.onStartElement(element, parentContext);

				xmlContext.getRuntime().addRuntimeListener(new RuntimeListenerAdapter() {
					@Override
					public void afterInit(RuntimeEvent event)
							throws ArooaException {
						if (error[0] == 1) {
							throw new ArooaException(
									"Property element can not have attributes, the problem configuration is:\n" +
									handler.getXml());
						}
						else if (error[0] == 2) {
							throw new ArooaException("Unrecognised property element [" +
									element.getTag() + "], the problem configuration is:\n" +
									handler.getXml());
						}
						else {
							throw new IllegalStateException("Error " + error[0] + " not known.");
						}
					}
				});

				return xmlContext;
			}

			return propertyContext;
		};
	}
	
	private ArooaContext getPropertyContext(String propertyName) {
		
		if (propertyContexts == null) {
			propertyContexts = new HashMap<>();

			if (instance.children() == null) {
				return null;
			}
			
			for (DesignProperty property: instance.children()) {
				
				if (! (property instanceof DesignElementProperty)) {
					continue;
				}
				
				propertyContexts.put(property.property(), 
						((DesignElementProperty) property).getArooaContext());
			}
		}
		
		return propertyContexts.get(propertyName);
	}

	private DesignTextProperty getTextProperty() {
		for (DesignProperty property: instance.children()) {
			if (property instanceof DesignTextProperty) {
				return (DesignTextProperty) property;
			}
		}
		return null;
	}
}
