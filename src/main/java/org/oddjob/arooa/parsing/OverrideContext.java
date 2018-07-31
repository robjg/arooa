package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

/**
 * Wraps an {@link ArooaContext} to allow behaviour to be altered. This is 
 * a classic application of the Adaptor Pattern.
 * 
 * @author rob
 *
 */
public class OverrideContext implements ArooaContext {

	private final ArooaContext existingContext;
	 
	private final ConfigurationNode configurationNode = new ConfigurationNode() {

		public void addNodeListener(ConfigurationNodeListener listener) {
			existingContext.getConfigurationNode().addNodeListener(listener);
		}

		public void addText(String text) {
			existingContext.getConfigurationNode().addText(text);		
		}

		public ArooaContext getContext() {
			return OverrideContext.this;
		}

		public int indexOf(ConfigurationNode child) {
			return existingContext.getConfigurationNode().indexOf(child);
		}

		public int insertChild(ConfigurationNode child) {
			return existingContext.getConfigurationNode().insertChild(child);
		}

		public void removeChild(int index) {
			existingContext.getConfigurationNode().removeChild(index);
		}

		public void removeNodeListener(ConfigurationNodeListener listener) {
			existingContext.getConfigurationNode().removeNodeListener(listener);
		}

		public void setInsertPosition(int insertAt) {
			existingContext.getConfigurationNode().setInsertPosition(insertAt);
		}

		public ConfigurationHandle parse(ArooaContext parentContext)
				throws ArooaParseException {
			return existingContext.getConfigurationNode().parse(parentContext);
		}
		
	};
	
	public OverrideContext(ArooaContext context) {
		this.existingContext = context;
	}
	
	protected ArooaContext getExistingContext() {
		return existingContext;
	}

	public ArooaType getArooaType() {
		return existingContext.getArooaType();
	}
	
	public ArooaContext getParent() {
		return existingContext.getParent();
	}
	
	public ArooaHandler getArooaHandler() {
		return existingContext.getArooaHandler();
	}
	
	public RuntimeConfiguration getRuntime() {
		return existingContext.getRuntime();
	}
	
	public PrefixMappings getPrefixMappings() {
		return existingContext.getPrefixMappings();
	}
	
	public ConfigurationNode getConfigurationNode() {
		return configurationNode;
	}
	
	public ArooaSession getSession() {
		return existingContext.getSession();
	}
	
}
