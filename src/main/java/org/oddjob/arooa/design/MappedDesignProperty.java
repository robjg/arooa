package org.oddjob.arooa.design;

import org.oddjob.arooa.ArooaConstants;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.PropertyContext.DesignSetter;
import org.oddjob.arooa.design.screem.MultiTypeTable;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapped Design Property. This design property keeps track of
 * the instances and the keys.
 * 
 * @author rob
 *
 */
public class MappedDesignProperty extends DesignPropertyBase {

	private final List<DesignInstance> instances =
		new ArrayList<>();

	private final Map<DesignInstance, String> keys =
			new HashMap<>();

	/**
	 * Constructor.
	 * 
	 * @param property
	 * @param propertyClass
	 * @param type
	 * @param parent
	 */
	public MappedDesignProperty(String property,
			Class<?> propertyClass,
			ArooaType type,
			DesignInstance parent) {
		super(property, propertyClass, type, parent);
	}
	
	/**
	 * Constructor
	 * 
	 * @param property
	 * @param parent
	 * @throws ArooaPropertyException
	 */
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

			// Element needs to be passed back. This is a bug.
			element = element.removeAttribute(ArooaConstants.KEY_PROPERTY);
		}
		
		return new MappedDesignSetter(key);
	}

	/**
	 * Used during parsing. Wraps the instance with it's key
	 */
	class MappedDesignSetter implements DesignSetter {
		
		private final String key;

		public MappedDesignSetter(String key) {
			this.key = key;
		}
		
		public void setDesign(int index, DesignInstance design) {
			keys.put(design, key);
			synchronizedInsert(index, design);
		}
	}
	
	
	void insertInstance(int index, DesignInstance de) {

		if (de == null) {
			DesignInstance instance = instances.remove(index);
			keys.remove(instance);
		}
		else {
			if (index == -1) {
				instances.add(de);
			}
			else {
				instances.add(index, de);
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
				return keys.get(instances.get(index));
			}
			public void setKey(int index, String value) {
				keys.put(instances.get(index), value);
			}
		});
		return table;
	}
	
	public boolean isPopulated() {
		return instances.size() > 0;
	}
	
	@Override
	String getKey(DesignInstance instance) {
		return keys.get(instance);
	}

}
