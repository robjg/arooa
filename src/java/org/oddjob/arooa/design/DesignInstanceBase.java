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
 * A DesignAdult may or may not be capable of having children.
 * If a DesignAdult does have children they must be design
 * elements.
 * 
 */
abstract class DesignInstanceBase implements DesignInstance {

	private final ArooaElement element;
		
	private final ArooaContext context;

	private String id;
	
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
				setId(attributes.get(ArooaConstants.ID_PROPERTY));
				continue;
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

	protected abstract DesignProperty[] children();

	public ArooaContext getArooaContext() {
		return context;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return tag().toString();
	}
}
