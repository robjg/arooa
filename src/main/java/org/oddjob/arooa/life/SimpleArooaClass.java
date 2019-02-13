package org.oddjob.arooa.life;

import org.oddjob.arooa.beanutils.DynaArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaInstantiationException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Wrapper for a standard Java class.
 *
 * @see DynaArooaClass
 */
public class SimpleArooaClass implements ArooaClass {

	private final Class<?> forClass;
	
	public SimpleArooaClass(Class<?> forClass) {
		this.forClass = forClass;
	}
	
	public Class<?> forClass() {
		return forClass;
	}
	
	public Object newInstance() throws ArooaInstantiationException {
		try {
			return forClass.newInstance();
		}
		catch (Exception e) {
			throw new ArooaInstantiationException(e);
		}
	}
	
	@Override
	public BeanOverview getBeanOverview(PropertyAccessor accessor) {
		return accessor.getBeanOverview(forClass);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SimpleArooaClass)) {
			return false;
		}
		return ((SimpleArooaClass) obj).forClass == this.forClass;
	}
	
	@Override
	public int hashCode() {
		return forClass.hashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + forClass;
	}
}
