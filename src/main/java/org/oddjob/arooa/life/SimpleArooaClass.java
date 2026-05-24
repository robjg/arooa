package org.oddjob.arooa.life;

import org.oddjob.arooa.beanutils.DynaArooaClass;
import org.oddjob.arooa.convert.TypeArooaUtils;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaInstantiationException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.lang.reflect.Type;

/**
 * Wrapper for a standard Java class.
 *
 * @see DynaArooaClass
 */
public class SimpleArooaClass implements ArooaClass {

	private final Class<?> forClass;

	private final Type type;

	public SimpleArooaClass(Class<?> forClass) {
		this.forClass = forClass;
		this.type = forClass;
	}

	public SimpleArooaClass(Type type) {
		this.type = type;
		this.forClass = TypeArooaUtils.rawType(type);
	}

	public Class<?> forClass() {
		return forClass;
	}

	@Override
	public Type getType() {
		return type;
	}

	public Object newInstance() throws ArooaInstantiationException {
		try {
			return forClass.getConstructor().newInstance();
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
