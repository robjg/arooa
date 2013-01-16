package org.oddjob.arooa.beanutils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

public class MagicBeanClass implements Serializable, DynaClass {
	private static final long serialVersionUID = 2010030100L;
	
	private final DynaProperty[] properties;
	
	private final String name;
	
	private final Map<String, DynaProperty> map = 
		new HashMap<String, DynaProperty>();
	
	public MagicBeanClass(DynaProperty[] properties, 
			String name) {
		this.properties = properties;
		this.name = name;
		
		for (DynaProperty property : properties) {
			map.put(property.getName(), property);
		}
	}
	
	@Override
	public DynaProperty[] getDynaProperties() {
		return properties;
	}
	
	@Override
	public DynaProperty getDynaProperty(String name) {
		return map.get(name);
	}
	
	@Override
	public String getName() {
		return MagicBean.class.getName() + ":" + name;
	}
	
	@Override
	public DynaBean newInstance() throws IllegalAccessException,
			InstantiationException {
		return new MagicBean(this);
	}
	
	public String getMagicBeanName() {
		return name;
	}
}
