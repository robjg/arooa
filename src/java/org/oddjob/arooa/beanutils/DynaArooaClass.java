package org.oddjob.arooa.beanutils;

import org.apache.commons.beanutils.DynaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaInstantiationException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

public class DynaArooaClass implements ArooaClass {
	
	private final DynaClass dynaClass;

	private final Class<?> forClass;
	
	public DynaArooaClass(DynaClass dynaClass, Class<?> forClass) {
		this.dynaClass = dynaClass;
		this.forClass = forClass;
	}

	@Override
	public Class<?> forClass() {
		return forClass;
	}
	
	public Object newInstance() throws ArooaInstantiationException {
		try {
			return dynaClass.newInstance();
		} catch (IllegalAccessException e) {
			throw new ArooaInstantiationException(e);
		} catch (InstantiationException e) {
			throw new ArooaInstantiationException(e);
		}
	}
	
	@Override
	public BeanOverview getBeanOverview(PropertyAccessor accessor) {
		return new DynaBeanOverview(dynaClass);	
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DynaArooaClass)) {
			return false;
		}
		return ((DynaArooaClass) obj).dynaClass.getName().equals(
				this.dynaClass.getName());
	}
	
	@Override
	public int hashCode() {
		return dynaClass.getName().hashCode();
	}
	
	public DynaClass getDynaClass() {
		return dynaClass;
	}
}
