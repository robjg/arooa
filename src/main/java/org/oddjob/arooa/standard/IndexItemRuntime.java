/**
 * 
 */
package org.oddjob.arooa.standard;

import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaPropertyException;

class IndexItemRuntime extends InstanceRuntime {
	
	IndexItemRuntime(
			InstanceConfiguration item,
			ArooaContext parentContext) {
		super(item, parentContext);
	}
	
	ParentPropertySetter getParentPropertySetter() {
		return new ParentPropertySetter() {
			public void parentSetProperty(Object value) 
			throws ArooaPropertyException {
				int index = getParentContext().getConfigurationNode().indexOf(
							getContext().getConfigurationNode());
				
				getParentContext().getRuntime().setIndexedProperty(
						null, 
						index,
						value);
			}
		};
	}
	
}