/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConstants;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.deploy.BeanDescriptorHelper;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.QTag;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Base implementation for {@link DesignInstances}.
 */
abstract class DesignInstanceBase implements ParsableDesignInstance {

	private final ArooaElement element;
		
	private final ArooaContext context;

	/**
	 * Utility method for discovering the {@link ArooaClass} for an 
	 * {@link ArooaElement}.
	 */
	static class ClassFinder {
		ArooaClass forElement(ArooaElement element, 
				ArooaContext parentContext) {
			ArooaClass theClass = null;
			
			ArooaDescriptor descriptor =
				parentContext.getSession().getArooaDescriptor();
			
			ElementMappings mappings = 
				descriptor.getElementMappings();
			
			if (mappings != null) {
				theClass = mappings.mappingFor(element, 
					new InstantiationContext(parentContext));
			}
			
			if (theClass == null) {
				throw new NullPointerException("No Class for Element [" +
						element + "]");
			}
			
			return theClass;
		}
	}
	
	/**
	 * Constructor.
	 * 
	 * @param element
	 * @param classIdentifier
	 * @param parentContext
	 */
	public DesignInstanceBase(ArooaElement element, 
			ArooaClass classIdentifier, ArooaContext parentContext) {
		
		ArooaSession session = parentContext.getSession();
		
		PropertyAccessor accessor = parentContext.getSession(
				).getTools().getPropertyAccessor();
		
		ArooaBeanDescriptor beanDescriptor = 
			session.getArooaDescriptor().getBeanDescriptor(
					classIdentifier, accessor);
		
		ArooaAttributes attributes = element.getAttributes(); 
		for (String attributeName : attributes.getAttributNames()) {
			if (ArooaConstants.ID_PROPERTY.equals(attributeName)) {
				if (this instanceof DesignComponentBase) {
					continue;
				}
			}
			if (ArooaConstants.KEY_PROPERTY.equals(attributeName)) {
				continue;
			}
			
			if (! new BeanDescriptorHelper(
					beanDescriptor).isAttribute(attributeName)) {
				
				throw new ArooaException(attributeName + " is not an attribute of " + 
						classIdentifier);
			}
		}
		
		this.element = element;
		
		this.context = new DesignInstanceContext(this, 
				classIdentifier, parentContext);
		
	}
	
	public ArooaElement element() {
		return element;
	}
	
	public QTag tag() {
		return InstanceSupport.tagFor(this);
	}

	public ArooaContext getArooaContext() {
		return context;
	}
	
	@Override
	public String toString() {
		return tag().toString();
	}
}
