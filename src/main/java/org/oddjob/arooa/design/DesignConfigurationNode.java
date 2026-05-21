package org.oddjob.arooa.design;

import org.oddjob.arooa.parsing.AbstractConfigurationNode;
import org.oddjob.arooa.parsing.ParseContext;

abstract class DesignConfigurationNode<P extends ParseContext<P>> extends AbstractConfigurationNode<P> {
		
	public String getText() {
		throw new UnsupportedOperationException();
	}
}
