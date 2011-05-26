package org.oddjob.arooa.design;

import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

abstract public class DesignValueBase extends DesignInstanceBase {
	
	public DesignValueBase(ArooaElement element, ArooaContext parentContext) {
		super(element, 
				new ClassFinder().forElement(element, parentContext),
				parentContext);
	}

	public DesignValueBase(ArooaElement element,
			ArooaClass classIdentifier, ArooaContext parentContext) {
		super(element, classIdentifier, parentContext);
	}
	
}
