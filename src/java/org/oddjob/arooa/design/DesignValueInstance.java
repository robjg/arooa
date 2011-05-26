/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design;

import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * A DesignElementType is a DesignElement which can be created
 * from a factory. These are the configuration elements for the 
 * ValueType objects that appear in Variables and Lists.
 * 
 * @see GenericDesignFactory
 * 
 * @author Rob Gordon.
 */
class DesignValueInstance extends DesignInstanceBase 
implements GenericDesignInstance {
		
	private DesignProperty[] children;
	
	/**
	 * Constructor.
	 */
	public DesignValueInstance(ArooaElement element, 
			ArooaClass runtimeClass, ArooaContext parentContext) {
		super(element, runtimeClass, parentContext);
	}
	
	public void children(DesignProperty[] children) {
		this.children = children;
	}
	
	public DesignProperty[] children() {
		return children;
	}
	
	/**
	 * The method is overridden by sub classes which have a detailed
	 * definition for their configuration.
	 * 
	 * @return The DesignDefinition for their configuration.
	 */
	public Form detail() {
		return GenericFormFactory.createForm(this);
	}
}
