package org.oddjob.arooa.design.etc;

import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaPropertyException;

/**
 * {@link DesignFactory} that creates a  Design for XML.
 * 
 * @see UnknownComponent
 * 
 * @author rob
 *
 */
public class UnknownComponentDF implements DesignFactory { 
	
	@Override
	public DesignInstance createDesign(ArooaElement element,
			ArooaContext parentContext) throws ArooaPropertyException {
		return new UnknownComponent(element, parentContext);
	}

}
