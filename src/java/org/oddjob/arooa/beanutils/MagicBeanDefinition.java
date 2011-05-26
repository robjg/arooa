package org.oddjob.arooa.beanutils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * @oddjob.description Definition for a Magic Bean, which is a bean that can be defined 
 * dynamically.
 * 
 * @author rob
 *
 */
public class MagicBeanDefinition {
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The name of the bean. This is also the 
	 * element name when the bean is used. 
	 * @oddjob.required Yes.
	 */
	private String name;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The bean properties. This is a list
	 * of {@link MagicBeanProperty}s.
	 * @oddjob.required No.
	 */
	private final List<MagicBeanProperty> properties = 
		new ArrayList<MagicBeanProperty>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProperties(int index, MagicBeanProperty property) {
		
		if (property == null) {
			properties.remove(index);
		}
		else {
			properties.add(index, property);
		}
	}
	
	public MagicBeanProperty getProperties(int index) {
		return properties.get(index);
	}
	
	public ArooaClass createMagic(ClassLoader loader) {
		if (name == null) {
			throw new IllegalStateException(
					"A Magic Bean Definition must have a name.");
		}
		
		DynaProperty[] dynas = new DynaProperty[properties.size()];
		
		for (int i = 0; i < dynas.length; ++i) {
			MagicBeanProperty prop = properties.get(i);
			
			String className = prop.getType();
			Class<?> cl;
			if (className == null) {
				cl = String.class;
			}
			else {
				try {
					cl = Class.forName(className, true, loader);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("For MagicBean class " + 
							name + ", property " + prop.getName(), e);
				}
			}
			
			dynas[i] = new DynaProperty(prop.getName(), cl);
		}
		
		DynaClass dynaClass = new MagicBeanClass(dynas, name);
		
		return new DynaArooaClass(dynaClass, MagicBean.class);
	}
}
