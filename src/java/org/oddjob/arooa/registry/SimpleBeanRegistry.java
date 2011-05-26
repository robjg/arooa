/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Register components by id and look them up by path. A ComponentRegistry is a
 * hierarchy with child registery. The path identifies in the hierarchy where
 * the component resides.
 * <p>
 * This class is thread safe. Beans may be registered and retrieved by
 * different threads.
 * 
 * @author Rob Gordon.
 */
public class SimpleBeanRegistry implements BeanRegistry {

	public static final String RESERVED_CHARACTERS = ".[]()" +
			Path.PATH_SEPARATOR;
	
	private static final Pattern reservedPattern = 
		Pattern.compile("[" + Pattern.quote(RESERVED_CHARACTERS) + "]");
	
	/** Maps ids to Components */
	private final Map<String, Object> ids = 
		new HashMap<String, Object>();

	/** Maps components to ids. */
	private final Map<Object, String> components = 
		new HashMap<Object, String>();
	
	private final PropertyAccessor propertyAcessor;
	
	private final ArooaConverter converter;
	
	/**
	 * Constructor for a local registry.
	 *
	 */
	public SimpleBeanRegistry() {
		this(null, null);
	}

	public SimpleBeanRegistry(PropertyAccessor propertyAccessor,
			ArooaConverter converter) {
		if (propertyAccessor == null) {
			this.propertyAcessor = new BeanUtilsPropertyAccessor();
		}
		else {
			this.propertyAcessor = propertyAccessor;
		}
		if (converter == null) {
			this.converter = new DefaultConverter();
		}
		else {
			this.converter = converter;
		}
	}
	
	public synchronized <T> Iterable<T> getAllByType(Class<T> type) {
		List<T> results = new ArrayList<T>();
		for (Object component : components.keySet()) {
			if (type.isInstance(component)) {
				results.add(type.cast(component));
			}
		}
		return results;
	}
	
    /**
     * Register an object. The id should not contain reserved characters.
     * 
     * @param id The id of the object.
     * @param object The object.
     */
	public synchronized void register(String id, Object component) 
	throws InvalidIdException {
		if (component == null) {
			throw new NullPointerException("Null component. id [" + id + "]");
		}
		if (id == null) {
			throw new NullPointerException("No id for component [" + component + "]");
		}
		
		Matcher reservedMatcher = reservedPattern.matcher(id);
		if (reservedMatcher.find()) {
			throw new InvalidIdException(
					id, "Id [" + id + 
					"] contains a reserverd character at position " +
					reservedMatcher.start() + ". Reserved characters: " +
					RESERVED_CHARACTERS);
		}
		
		if (ids.containsKey(id)) {
			// don't override existing registrations.
			return;
		}
		
	    ids.put(id, component);
	    components.put(component, id);
	    
	}

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.registry.BeanDirectory#getById(java.lang.String)
	 */
	public Object lookup(String path) 
	throws ArooaPropertyException {
		PathBreakdown breakdown = new PathBreakdown(path);
		Object bean = null;
		synchronized (this) {
			bean = ids.get(breakdown.getId());
		}
		if (bean == null) {
			return null;
		}
		if (breakdown.isNested()) {
			if (bean instanceof BeanDirectoryOwner) {
				BeanDirectory next = ((BeanDirectoryOwner) bean).provideBeanDirectory();
				if (next == null) {
					return null;
				}
				return next.lookup(breakdown.getNestedPath());
			}
			else {
				return null;
			}
		}
		else {
			if (breakdown.isProperty()) {
				return propertyAcessor.getProperty(bean, breakdown.getProperty()); 
			}
			else {
				return bean;
			}
		}
	}
	
	public <T> T lookup(String path, Class<T> required) 
	throws ArooaPropertyException, ArooaConversionException {
		PathBreakdown breakdown = new PathBreakdown(path);
		Object bean = null;
		synchronized (this) {
			bean = ids.get(breakdown.getId());
		}
		if (bean == null) {
			return null;
		}
		if (breakdown.isNested()) {
			if (bean instanceof BeanDirectoryOwner) {
				BeanDirectory next = ((BeanDirectoryOwner) bean).provideBeanDirectory();
				if (next == null) {
					return null;
				}
				return next.lookup(breakdown.getNestedPath(), required);
			}
			else {
				return null;
			}
		}
		else {
			Object value;
			if (breakdown.isProperty()) {
				value = propertyAcessor.getProperty(bean, breakdown.getProperty()); 
			}
			else {
				value = bean;
			}
			return converter.convert(value, required);
		}
	}
	
	/**
	 * Find the id for the given component.
	 * 
	 * @param component The component.
	 * @return The id or null if none can be found.
	 */
	public synchronized String getIdFor(Object component) {
	    return components.get(component);
	}
	
	/**
	 * Remove a component from the registry if it exists.
	 * 
	 * @param component The compnent.
	 */
	public synchronized void remove(Object component) {
		String id = components.remove(component);
		ids.remove(id);
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public synchronized String toString() {
		return "BeanRegistry: size [" + ids.size() + "]";
	}
}
