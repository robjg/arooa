/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.registry;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Provide a BeanDirectory from some mapping functions.
 *
 * @author Rob Gordon.
 */
public class SimpleBeanDirectory implements BeanDirectory {

	/** Maps ids to Components */
	private final Function<? super String, ?> ids;

	/** Maps components to ids. */
	private final Function<? super Object, ? extends String> toId;

	/** Stream of all the components */
	private final Supplier<Stream<?>> components;

	private final PropertyAccessor propertyAccessor;

	private final ArooaConverter converter;

	public SimpleBeanDirectory(Function<? super String, ?> ids,
							   Function<? super Object, ? extends String> toId,
							   Supplier<Stream<?>> components,
							   PropertyAccessor propertyAccessor,
							   ArooaConverter converter) {
		this.ids = ids;
		this.toId = toId;
		this.components = components;
		this.propertyAccessor = propertyAccessor;
		this.converter = converter;
	}


	@Override
	public synchronized <T> Iterable<T> getAllByType(Class<T> type) {
		return () -> components.get().filter( type::isInstance )
				.map(type::cast)
				.iterator();
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.registry.BeanDirectory#getById(java.lang.String)
	 */
	public Object lookup(String path) 
	throws ArooaPropertyException {
		PathBreakdown breakdown = new PathBreakdown(path);
		Object bean;
		synchronized (this) {
			bean = ids.apply(breakdown.getId());
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
				return propertyAccessor.getProperty(bean, breakdown.getProperty());
			}
			else {
				return bean;
			}
		}
	}
	
	public <T> T lookup(String path, Class<T> required) 
	throws ArooaPropertyException, ArooaConversionException {

		return converter.convert(lookup(path), required);
	}
	
	/**
	 * Find the id for the given component.
	 * 
	 * @param component The component.
	 * @return The id or null if none can be found.
	 */
	public synchronized String getIdFor(Object component) {
	    return toId.apply(component);
	}
	
}
