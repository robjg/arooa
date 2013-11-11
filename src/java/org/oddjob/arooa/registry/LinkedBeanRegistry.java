package org.oddjob.arooa.registry;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * A {@link BeanRegistry} that will attempt to find a bean value in an
 * existing {@link BeanDirectory} if it hasn't been registered with this
 * registry.
 * 
 * @author rob
 *
 */
public class LinkedBeanRegistry extends SimpleBeanRegistry {

	private final BeanDirectory existingDirectory;    	
    	    	
	/**
	 * Constructor.
	 * 
	 * @param existingDirectory
	 * @param propertyAccessor
	 * @param converter
	 */
	public LinkedBeanRegistry (BeanDirectory existingDirectory,
			PropertyAccessor propertyAccessor,
			ArooaConverter converter) {

		super(propertyAccessor, converter);
		this.existingDirectory = existingDirectory;
	}

	/**
	 * A constructor that takes takes the existing directory and tools
	 * from the given session.
	 * 
	 * @param existingSession
	 */
	public LinkedBeanRegistry(ArooaSession existingSession) {
		this(existingSession.getBeanRegistry(), 
				existingSession.getTools().getPropertyAccessor(),
				existingSession.getTools().getArooaConverter());
	}
	
	/**
	 * First try our local registry then the parent.
	 * 
	 */
	public Object lookup(String path) {
		Object component = super.lookup(path);
		if (component == null) {
			return existingDirectory.lookup(path);
		}
		return component;
	}

	@Override
	public <T> T lookup(String path, Class<T> required)
			throws ArooaConversionException {
		T component = super.lookup(path, required);
		if (component == null) {
			return existingDirectory.lookup(path, required);
		}
		return component;
	}

	public String getIdFor(Object bean) {
		String id = super.getIdFor(bean);
		if (id == null) {
			return existingDirectory.getIdFor(bean);
		}
		return id;
	}

	@Override
	public synchronized <T> Iterable<T> getAllByType(Class<T> type) {
		List<T> results = new ArrayList<T>();
		for (T t : super.getAllByType(type)) {
			results.add(t);
		}
		for (T t : existingDirectory.getAllByType(type)) {
			results.add(t);
		}
		return results;
	}
}
