package org.oddjob.arooa.standard;

import org.oddjob.arooa.parsing.ArooaContext;


class RootRuntime extends InstanceRuntime {		
		
	public RootRuntime(
			InstanceConfiguration instance,
			ArooaContext rootContext) {
		super(instance, rootContext);
	}

	@Override
	ParentPropertySetter getParentPropertySetter() {
		return new ParentPropertySetter() {
			public void parentSetProperty(Object value) {
				// Do nothing
			}
		};
	}

}
