package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.runtime.ConfigurationNode;

/**
 * Wraps an {@link ArooaContext} to allow behaviour to be altered. This is 
 * a classic application of the Adaptor Pattern.
 * 
 * @author rob
 *
 */
public class OverrideContext<P extends ParseContext<P>> implements ParseContext<P> {

	private final P existingContext;

	public OverrideContext(P context) {
		this.existingContext = context;
	}
	
	protected P getExistingContext() {
		return existingContext;
	}

	@Override
	public ArooaType getArooaType() {
		return existingContext.getArooaType();
	}
	
	public P getParent() {
		return existingContext.getParent();
	}

	@Override
	public ElementHandler<P> getElementHandler() {
		return existingContext.getElementHandler();
	}

	@Override
	public PrefixMappings getPrefixMappings() {
		return existingContext.getPrefixMappings();
	}

	@Override
	public ConfigurationNode<P> getConfigurationNode() {
		return existingContext.getConfigurationNode();
	}

	@Override
	public void destroy() {
		existingContext.destroy();
	}


}
