package org.oddjob.arooa.standard;

import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;

class InstanceConfigurationNode extends StandardConfigurationNode {

	private final InstanceRuntime instance;
	
	public InstanceConfigurationNode(ArooaElement element,
			InstanceRuntime instance) {
		super(element);
		this.instance = instance;
	}
	
	public void addText(String text) {
		instance.getInstance().addText(text);
	}

	@Override
	public String getText() {
		return instance.getInstance().getText();
	}
	
	public ArooaContext getContext() {
		return instance.getContext();
	}
}
