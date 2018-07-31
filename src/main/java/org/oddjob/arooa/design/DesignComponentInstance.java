package org.oddjob.arooa.design;

import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * Used by the {@link GenericDesignFactory} for the design time representation
 * of a component.
 * 
 * @see GenericDesignFactory
 * 
 * @author rob
 *
 */
class DesignComponentInstance extends DesignComponentBase 
implements GenericDesignInstance {

	private DesignProperty[] children;
	
	public DesignComponentInstance(ArooaElement element, 
			ArooaClass classIdentifier, ArooaContext parentContext) {
		super(element, classIdentifier, parentContext);
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
		

	@Override
	public String toString() {
		return tag().toString();
	}
}
