package org.oddjob.arooa.design;

import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

import java.util.Properties;

/**
 * Shared implementation for designs that are values.
 *
 * @see DesignComponentBase
 */
abstract public class DesignValueBase extends DesignInstanceBase {
	
	public DesignValueBase(ArooaElement element,
						   ArooaContext parentContext) {
		this(element,
				new ClassFinder().forElement(element, parentContext),
				parentContext);
	}

	public DesignValueBase(ArooaElement element,
			ArooaClass classIdentifier, ArooaContext parentContext) {
		super(element, classIdentifier, parentContext);
	}
}
