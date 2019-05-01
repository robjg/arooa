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

	/** The name of the property. */
	private final String property;
	
	/** The context for this node. */
	private final ArooaContext arooaContext;
	
	/** listener notified when the design changes. */
	private final List<DesignListener> listeners =
			new ArrayList<>();
	
	/**
	 * Constructor that creates a {@link DesignProperty} using the given
	 * property class and type and only uses the parent for it's context.
	 * This is used by the {@link GenericDesignFactory}.
	 * 
	 * @param property The property name.
	 * @param propertyClass The property class.
	 * @param type The type (component/value) of the property.
	 * @param parent The parent DesignInstance.
	 */
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
	
	/**
	 * Constructor that creates a {@link DesignProperty} from the 
	 * characteristics of the property given by the parent instance.
	 * 
	 * @param property The property name.
	 * @param parent The parent DesignInstance.
	 * 
	 * @throws ArooaPropertyException
	 */
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
	 * @return The thing that will insert the design into this property.
	 */
	DesignSetter getDesignSetter(final ArooaElement element) {
		return this::synchronizedInsert;
	}
	
	/**
	 * Used During parsing. 
	 * 
	 * @see MappedDesignProperty
	 * 
	 * @return The key of this property if it is a mapped property. Null
	 * otherwise.
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
		
	/**
	 * Subclasses must override this to support adding the designs
	 * of the values of this property.
	 * 
	 * @param index The index of the instance for list and mapped 
	 * properties. 0 for simple properties.
	 * @param instance The instance.
	 */
	abstract void insertInstance(int index, DesignInstance instance);

	/**
	 * Overridden by subclasses to provide the number of design instances 
	 * for this property.
	 * 
	 * @return The number of design instances.
	 */
	abstract int instanceCount();
	
	/***
	 * Overridden by subclasses to provide the design of the property value
	 * at the index.
	 * 
	 * @param index The property value's index.
	 * 
	 * @return The design. Never null.
	 */
	abstract DesignInstance instanceAt(int index);

	@Override
	public void addDesignListener(DesignListener listener) {
		synchronized (listeners) {
			for (int i = 0; i < instanceCount(); ++i) {
				listener.childAdded(
						new DesignStructureEvent(this, instanceAt(i), i));
			}
			listeners.add(listener);
		}
	}

	@Override
	public void removeDesignListener(DesignListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * Used by {@link DesignSetter}s to do the actual design insertion. 
	 * This method notifies design listeners of the design change.
	 * 
	 * @param index
	 * @param design
	 */
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
