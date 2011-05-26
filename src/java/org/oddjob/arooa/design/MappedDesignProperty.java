package org.oddjob.arooa.design;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.arooa.ArooaConstants;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.PropertyContext.DesignSetter;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.MultiTypeTable;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Mapped Design Property.
 * 
 * @author rob
 *
 */
public class MappedDesignProperty extends DesignPropertyBase {

	private final List<InstanceWrapper> instances = 
		new ArrayList<InstanceWrapper>();	
		
	public MappedDesignProperty(String property,
			Class<?> propertyClass,
			ArooaType type,
			DesignInstance parent) {
		super(property, propertyClass, type, parent);
	}
	
	public MappedDesignProperty(String property,
			DesignInstance parent) 
	throws ArooaPropertyException {
		super(property, parent);
	}
	
	DesignSetter getDesignSetter(ArooaElement element) {
		String key = element.getAttributes().get(
				ArooaConstants.KEY_PROPERTY);

		ArooaContext context = getArooaContext();
		
		PropertyAccessor propertyAccessor = 
			context.getSession(
				).getTools().getPropertyAccessor();

		ArooaClass arooaClass = context.getRuntime().getClassIdentifier();
		
		BeanOverview beanOverview = arooaClass.getBeanOverview(
				propertyAccessor);

		if (!beanOverview.hasWriteableProperty(
				ArooaConstants.KEY_PROPERTY)) {

			element = element.removeAttribute(ArooaConstants.KEY_PROPERTY);
		}
		
		return new MappedDesignSetter(key);
	}

	class MappedDesignSetter implements DesignSetter {
		
		private final String key;

		public MappedDesignSetter(String key) {
			this.key = key;
		}
		
		public void setDesign(int index, DesignInstance design) {
			synchronizedInsert(index,  
					new InstanceWrapper(design, key));
		}
	}
	
	
	void insertInstance(int index, DesignInstance de) {
		InstanceWrapper wrapper = (InstanceWrapper) de;
		
		if (de == null) {
			instances.remove(index);
		}
		else {
			if (index == -1) {
				instances.add(wrapper);
			}
			else {
				instances.add(index, wrapper);
			}
		}
	}
	
	public int instanceCount() {
		return instances.size();
	}
	
	public DesignInstance instanceAt(int index) {
		return instances.get(index);
	}
	
	public MultiTypeTable view() {
		MultiTypeTable table = new MultiTypeTable(this);
		table.setKeyAccess(new MultiTypeTable.KeyAccess() {
			public String getKey(int index) {
				return instances.get(index).key;
			}
			public void setKey(int index, String value) {
				instances.get(index).key = value;
			}
		});
		return table;
	}
	
	public boolean isPopulated() {
		return instances.size() > 0;
	}
	
	@Override
	String getKey(DesignInstance instance) {
		return ((InstanceWrapper) instance).key;
	}
	
	static class InstanceWrapper implements DesignInstance {
	
		private final DesignInstance wrapping;

		private String key;

		public InstanceWrapper(DesignInstance wrapping, String key) {
			this.key = key;
			this.wrapping = wrapping;
		}
		
		public Form detail() {
			return wrapping.detail();
		}

		public ArooaElement element() {
			return wrapping.element();
		}
		
		public ArooaContext getArooaContext() {
			return wrapping.getArooaContext();
		}
		
		DesignInstance getWrapping() {
			return wrapping;
		}
		
		@Override
		public String getId() {
			return wrapping.getId();
		}
		
		@Override
		public void setId(String id) {
			wrapping.setId(id);
		}
	}
	
}
