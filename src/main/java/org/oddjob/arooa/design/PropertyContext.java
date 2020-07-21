package org.oddjob.arooa.design;

import org.oddjob.arooa.*;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

class PropertyContext implements ArooaContext {

	private final DesignPropertyBase designProperty;
	private final ArooaClass propertyClass;
	private final ArooaContext parentContext;

	private final ArooaType type;
	
	private final RuntimeConfiguration runtime = new PropertyRuntime();
			
	/**
	 * Provided by the DesignProperty. Required for Mapped properties.
	 */
	interface DesignSetter {
		
		/**
		 * Set the DesignInstance in the parent DesignProperty.
		 * 
		 * @param index
		 * @param design
		 */
		void setDesign(int index, DesignInstance design);
		
	}
		
	private final InstanceHandler handler = new InstanceHandler();
		
	class InstanceHandler extends XMLFirstHandler {

		private DesignSetter setter;
		
		@Override
		public ArooaContext onStartElement(ArooaElement element,
				ArooaContext parentContext) 
		throws ArooaConfigurationException {
			setter = designProperty.getDesignSetter(element);
			return super.onStartElement(element, parentContext);
		}
		
		@Override
		DesignInstance goodDesign(ArooaElement element,
				ArooaContext parentContext) 
		throws ArooaPropertyException {
			DesignInstance design = 
				new DescriptorDesignFactory().createDesign(
					element, parentContext);
			if (design == null) {
				throw new NullPointerException("No Design For [" + 
						element + "].");
			}
			return design;
		}
		
		@Override
		void setDesign(int index, DesignInstance design) {
			setter.setDesign(index, design);
		}

		@Override
		void onBeforeInit() {
			ignoreInsert = true;
		}

		@Override
		void onAfterInit() {
			ignoreInsert = false;
		}
	}
	
	private boolean ignoreInsert = false;
	
	class PropertyRuntime extends DesignRuntime {
	
		@Override
		public void init() throws ArooaConfigurationException {
			fireBeforeInit();
			
			int index = parentContext.getConfigurationNode().indexOf(
					configurationNode);
			
			if (index < 0) {
				throw new IllegalStateException(
						"Configuration node not added to parent.");
			}
			
			parentContext.getRuntime().setIndexedProperty(null, index, designProperty);
			
			fireAfterInit();
		}
		
		@Override
		public void destroy() throws ArooaConfigurationException {
			fireBeforeDestroy();
			
			
			int index = parentContext.getConfigurationNode().indexOf(
					configurationNode);
			
			if (index < 0) {
				throw new IllegalStateException(
						"Configuration node not added to parent.");
			}
			
			parentContext.getRuntime().setIndexedProperty(null, index, null);
			
			fireAfterDestroy();
		}
		
		@Override
		public ArooaClass getClassIdentifier() {
			return propertyClass;
		}

		public void setProperty(String shouldBeNull, Object value)
		throws ArooaException {
			throw new UnsupportedOperationException("Used setIndexedProperty for Designs.");
		}

		public void setMappedProperty(String shouldBeNull, String key, Object value)
		throws ArooaException {
			throw new UnsupportedOperationException("Used setIndexedProperty for Designs.");
		}	
		
		public void setIndexedProperty(String shouldBeNull, int index, Object value)
		throws ArooaException {
			
			if (!ignoreInsert) {
				designProperty.synchronizedInsert(index, (DesignInstance) value);
			}
		}
	}


	private final ConfigurationNode<ArooaContext> configurationNode = new DesignConfigurationNode() {

		public ArooaContext getContext() {
			return PropertyContext.this;
		}
		
		public void addText(String text) {
			String trimmedText = text.trim(); 
			if (trimmedText.length() > 0) {
				throw new ArooaException("No text expected: " + trimmedText);				
			}
		}

		@Override
		public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
				throws ArooaParseException {
			
			if (this.children().length == 0) {
				return null;
			}
			
			ArooaElement element = new ArooaElement(
					designProperty.property());
			
			ElementHandler<P> handler = parentContext.getElementHandler();
			
			ParseHandle<P> parseHandle;
			try {
				parseHandle = handler.onStartElement(
					element, parentContext);
			}
			catch (ArooaConfigurationException e) {
	    		throw new ArooaParseException("Failed parsing property.", 
	    				new Location(designProperty.property(), 0, 0), e);
			}

			P nextContext = parseHandle.getContext();

			// Do this rather than configuration node children because of
			// mapped properties.
			for (int i = 0; i < designProperty.instanceCount(); ++i) {
				final DesignInstance design = designProperty.instanceAt(i);
					
				ArooaConfiguration configurationNode = design
						.getArooaContext().getConfigurationNode();

				configurationNode.parse(nextContext);
			}

			parseHandle.init();

    		return new PropertyConfigurationHandle<>(nextContext);
		}
	};

	static class PropertyConfigurationHandle<P extends ParseContext<P>>
			implements ConfigurationHandle<P> {

		private final P nextContext;
		
		public PropertyConfigurationHandle(P nextContext) {
			this.nextContext = nextContext;
		}
		
		public void save() throws ArooaParseException {
			throw new UnsupportedOperationException("Can only save instances not properties.");
		}
		
		public P getDocumentContext() {
			return nextContext;
		}
	}
	
	
	public PropertyContext(
			Class<?> propertyClass,
			DesignPropertyBase designProperty,
			ArooaType type,
			ArooaContext parentContext) {
		
		this.propertyClass = new SimpleArooaClass(propertyClass);
		this.designProperty = designProperty;
		this.type = type;
		this.parentContext = parentContext;
	}

	public ArooaType getArooaType() {
		return type;
	}
	
	public ArooaHandler getArooaHandler() {
		return handler;
	}

	public ArooaContext getParent() {
		return parentContext;
	}
	
	public PrefixMappings getPrefixMappings() {
		return parentContext.getPrefixMappings();
	}
		
	public RuntimeConfiguration getRuntime() {
		return runtime;
	}
	
	public ConfigurationNode<ArooaContext> getConfigurationNode() {
		return configurationNode;
	}
	
	public ArooaSession getSession() {
		return parentContext.getSession();
	}

	public String getKey(DesignInstance designInstance) {
		return designProperty.getKey(designInstance);
	}
}
