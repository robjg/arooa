package org.oddjob.arooa.design;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.deploy.BeanDescriptorHelper;
import org.oddjob.arooa.design.PropertyContext.DesignSetter;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

abstract class DesignPropertyBase implements DesignElementProperty {

	private final String property;
	
	private final ArooaContext arooaContext;
	
	private final List<DesignListener> listeners =
		new ArrayList<DesignListener>();
	
	DesignPropertyBase(String property,
			Class<?> propertyClass,
			ArooaType type,
			DesignInstance parent) {
		this.property = property;
		this.arooaContext = new PropertyContext(
				propertyClass, this, type, parent.getArooaContext());

		parent.getArooaContext().getConfigurationNode().insertChild(
				this.arooaContext.getConfigurationNode());
	}	
	
	DesignPropertyBase(String property,
			DesignInstance parent) 
	throws ArooaPropertyException {
		this.property = property;

		ArooaClass parentClass = parent.getArooaContext().getRuntime(
				).getClassIdentifier();
		
		ArooaSession session = parent.getArooaContext().getSession();
		
		PropertyAccessor propertyAccessor = session.getTools().getPropertyAccessor();

		ArooaBeanDescriptor beanDescriptor = 
			session.getArooaDescriptor().getBeanDescriptor(
				parentClass, propertyAccessor); 
		
		ArooaType type = new BeanDescriptorHelper(
				beanDescriptor).getArooaType(property);
		
		BeanOverview overview = parentClass.getBeanOverview(
				propertyAccessor);
		
		Class<?> propertyClass = overview.getPropertyType(property);

		this.arooaContext = new PropertyContext(
				propertyClass, this, type, parent.getArooaContext());
		
		parent.getArooaContext().getConfigurationNode().insertChild(
				this.arooaContext.getConfigurationNode());
	}	
	
	/**
	 * This allows the mapped property to extract the key. Not very
	 * elegant!
	 * 
	 * @param element
	 * @return
	 */
	DesignSetter getDesignSetter(final ArooaElement element) {
		return new DesignSetter() {
			public void setDesign(int index, DesignInstance design) {
				synchronizedInsert(index, design);
			}
		};
	}
	
	/**
	 * Used During parsing - used by MappedDesignProperty.
	 * @return
	 */
	String getKey(DesignInstance instance) {
		return null;
	}
	
	public String property() {
		return property;
	}
	
	public ArooaContext getArooaContext() {
		return arooaContext;
	}
		
	abstract void insertInstance(int index, DesignInstance instance);

	abstract int instanceCount();
	
	abstract DesignInstance instanceAt(int index);

	public void addDesignListener(DesignListener listener) {
		synchronized (listeners) {
			for (int i = 0; i < instanceCount(); ++i) {
				listener.childAdded(
						new DesignStructureEvent(this, instanceAt(i), i));
			}
			listeners.add(listener);
		}
	}
	
	public void removeDesignListener(DesignListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	void synchronizedInsert(int index, DesignInstance design) {
		synchronized (listeners) {
			DesignStructureEvent event;
			
			if (design == null) {
				event = new DesignStructureEvent(this, instanceAt(index), index);
			}
			else {
				event = new DesignStructureEvent(this, design, index);
			}
			
			insertInstance(index, design);
			
			if (design == null) {
				for (DesignListener listener: listeners) {
					listener.childRemoved(event);
				}
			}
			else {
				for (DesignListener listener: listeners) {
					listener.childAdded(event);
				}				
			}
		}
	}
}
