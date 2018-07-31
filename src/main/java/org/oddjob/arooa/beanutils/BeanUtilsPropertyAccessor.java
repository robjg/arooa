/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.beanutils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanAccessLanguageException;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaClassFactory;
import org.oddjob.arooa.reflect.ArooaClasses;
import org.oddjob.arooa.reflect.ArooaNoPropertyException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessException;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.reflect.PropertyExceptionBuilder;
import org.oddjob.arooa.reflect.PropertySetException;

/**
 * BeanUtilsBean with bespoke conversion and ArooaExceptions.
 */
public class BeanUtilsPropertyAccessor implements PropertyAccessor {
	private static final Logger logger = LoggerFactory.getLogger(BeanUtilsPropertyAccessor.class);
		
	static {
		
		final ArooaClassFactory<Object> simple = new ArooaClassFactory<Object>() {
			@Override
			public ArooaClass classFor(Object instance) {
				
				return new SimpleArooaClass(instance.getClass());
			}
		};
		
		final ArooaClassFactory<DynaBean> dyna = new ArooaClassFactory<DynaBean>() {
			
			@Override
			public ArooaClass classFor(DynaBean instance) {
				
				return new DynaArooaClass(
						instance.getDynaClass(),
						DynaBean.class);
			}
		};

		ArooaClasses.register(Object.class, simple);		
		ArooaClasses.register(DynaBean.class, dyna);
		
		// this is because the underlying implementation uses converter
		// which shouldn't be this classes fault!		
		ArooaClassFactory<ArooaValue> arooa = new ArooaClassFactory<ArooaValue>() {
			
			@Override
			public ArooaClass classFor(ArooaValue instance) {
	
				if (instance instanceof DynaClass) {
					return dyna.classFor((DynaBean) instance);
				}
				else {
					return simple.classFor(instance);
				}
			}
		};
		
		ArooaClasses.register(ArooaValue.class, arooa);
	}
	
	private final PropertyUtilsBean propertyUtilsBean;
	
	private final Map<Class<?>, BeanOverview> overviews;
	
	private final ArooaConverter converter;
	
	/**
	 * Constructor which takes a BeanUtilsBean and tag and location
	 * for exceptions.
	 * 
	 * @param bub The BeanUtilsBean.
	 */
	private BeanUtilsPropertyAccessor(ArooaConverter converter) {
		this.converter = converter;
		this.propertyUtilsBean = new PropertyUtilsBean();
		this.overviews =
			new HashMap<Class<?>, BeanOverview>();
	}
	
	public BeanUtilsPropertyAccessor() {
		this(null);
	}
	
	private BeanUtilsPropertyAccessor(
			ArooaConverter converter,
			PropertyUtilsBean propertyUtilsBean,
			Map<Class<?>, BeanOverview> overviews) {
		this.converter = converter;
		this.propertyUtilsBean = propertyUtilsBean;
		this.overviews = overviews;
	}

	public PropertyAccessor accessorWithConversions(
			ArooaConverter converter) {
		return new BeanUtilsPropertyAccessor(converter,
				propertyUtilsBean, overviews);
	}
	
	public ArooaConverter getConverter() {
		return converter;
	}
	
	class PropertySetter implements PropertyPath.FragmentVisitor {
		Object bean;
		Object on;
		String path;
		Object value;
		
		PropertySetter(Object bean, String path) {
			this.bean = bean;
			this.on = bean;
			this.path = path;
		}
				
		public void set(Object value) throws ArooaPropertyException {
			this.value = value;
			
			PropertyPath pp = new PropertyPath(path);
			pp.iterate(this);
		}
		
		public void onIntermediateProperty(String name) 
		throws ArooaPropertyException {
			if (on instanceof DynaBean) {
				on = ((DynaBean) on).get(name);
			} else {
				try {
					on = propertyUtilsBean.getSimpleProperty(on, name);
				} catch (Exception e) {
					throw new PropertyAccessException(bean, path, e);
				}
			}
			if (on == null) {
				throw new PropertyAccessException(bean, path, "Null value for [" + name + "]");
			}
		}
		
		public void onSimpleProperty(String name) 
		throws ArooaPropertyException {
			setSimpleProperty(on, name, value);
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.oddjob.arooa.reflect.PropertyPath.FragmentVisitor#onIndexedProperty(java.lang.String, int)
		 */
		public void onIndexedProperty(String name, int index) 
		throws ArooaPropertyException {
			setIndexedProperty(on, name, index, value);
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.oddjob.arooa.reflect.PropertyPath.FragmentVisitor#onMappedProperty(java.lang.String, java.lang.String)
		 */
		public void onMappedProperty(String name, String key) 
		throws ArooaPropertyException {
			setMappedProperty(on, name, key, value);
		}
		
	}

	/**
	 * Set a property on a bean.
	 * 
	 * @param bean The bean. Must not be null.
	 * @param name The name. Must not be null.
	 * @param value The value. Can be null.
	 */
	public void setProperty(Object bean, String name, Object value) 
	throws ArooaPropertyException {
		if (bean == null) {
			throw new NullPointerException("Bean must not be null!");
		}
		if (name == null) {
			throw new NullPointerException("Property name must not be null");
		}
		
		logger.debug("Setting property [" + name + "] on [" 
				+ bean.getClass().getName() + "] value [" +  value + "]...");

		// We have to use our own setter because of the conversion.
		PropertySetter setter = new PropertySetter(bean, name);
		setter.set(value);
	}	

	
	/**
	 * Set a property on a bean.
	 * 
	 * @param bean The bean. Must not be null.
	 * @param name The name. Must not be null.
	 * @param value The value. Can be null.
	 * 
	 * @throws ArooaPropertyException 
	 */
	public void setSimpleProperty(Object bean, String name, Object value) 
	throws ArooaPropertyException {
		if (bean == null) {
			throw new NullPointerException("Bean must not be null!");
		}
		if (name == null) {
			throw new NullPointerException("Property name must not be null");
		}
		
		Class<?> type = getPropertyType(bean, name);
		logger.debug("Setting property [" + name + "] ("
				+ type.getName() + ") on [" 
				+ bean.getClass().getName() + "] value [" +  value + "]");
		try {
			propertyUtilsBean.setSimpleProperty(bean, name, 
					convert(value, type));
		} 
		catch (Exception e) {
			throw new PropertySetException(bean, name, type, value, e);
		}
	}	
	
	/**
	 * Set a mapped property on a bean.
	 * 
	 * @param bean The bean. Must not be null.
	 * @param name The name. Must not be null.
	 * @param key The mapped property's key. Must not be null.
	 * @param value The value. Can be null.
	 * 
	 * @throws PropertyAccessException 
	 */
	public void setMappedProperty(Object bean, String name, 
			String key, Object value) 
	throws ArooaPropertyException {
		if (bean == null) {
			throw new NullPointerException("Bean must not be null!");
		}
		if (name == null) {
			throw new NullPointerException("Property name must not be null");
		}
		if (key == null) {
			throw new NullPointerException("Key must not be null");
		}

		Class<?> type = getPropertyType(bean, name);
		logger.debug("Setting mapped property [" + name + "] ("
				+ type.getName() + ") key ["
			 + key + "] on [" 
				+ bean.getClass().getName() + "] value [" +  value + "]");
		try {
			Object converted = convert(value, type);
			
			propertyUtilsBean.setMappedProperty(bean, name, 
					key, converted);
		} 
		catch (Exception e) {
			throw new PropertyAccessException(bean, name, e);
		}
	}
	

	
	/**
	 * Set an indexed property on a bean.
	 * 
	 * @param bean The bean. Must not be null.
	 * @param name The name. Must not be null.
	 * @param index The indexed property's index. 0 based.
	 * @param value The value. Can be null.
	 * 
	 * @throws ArooaPropertyException 
	 */
	public void setIndexedProperty(Object bean, 
			String name, int index, Object value) 
	throws ArooaPropertyException {
		if (bean == null) {
			throw new NullPointerException("Bean must not be null!");
		}
		if (name == null) {
			throw new NullPointerException("Property name must not be null");
		}

		Class<?> type = getPropertyType(bean, name);
		logger.debug("Setting index property [" + name + "] ("
				+ type.getName() + ") index ["
			 + index + "] on [" 
				+ bean.getClass().getName() + "] value [" +  value + "]");
		try {
			propertyUtilsBean.setIndexedProperty(bean, 
					name, index, convert(value, type));
		} 
		catch (Exception e) {
			throw new PropertyAccessException(bean, name, e);
		}
	}
	
	Object convert(Object from, Class<?> type) throws NoConversionAvailableException, ConversionFailedException {
		if (converter == null) {
			return from;
		}
		return converter.convert(from, type);
	}
	
	/**
	 * Get the property type. 
	 * 
	 * @param bean
	 * @param name
	 * @return
	 */
	Class<?> getPropertyType(Object bean, String name) 
	throws ArooaPropertyException {
		
        if (bean instanceof DynaBean) {
        	
        	DynaClass dynaClass = ((DynaBean) bean).getDynaClass();
        	
            DynaProperty descriptor =
                    dynaClass.getDynaProperty(name);
            
            if (descriptor == null) {
                throw new ArooaNoPropertyException(name, 
                		bean.getClass(), 
                		new DynaBeanOverview(dynaClass).getProperties());
            }
            
            Class<?> type = descriptor.getContentType();
            
            if (type != null) {
            	return type;
            }
            
            type = descriptor.getType();
            
            if (type == null) {
            	// this must surely be an error by the writer of the DynaClass implementation.
                throw new NullPointerException("Descriptor exists for property [" + 
                		name + "] but the property type is null.");
            } 
            return type;
        }
        
		Class<?> type = null;
		try {			
			type = propertyUtilsBean.getPropertyType(bean, name);
		} 
		catch (Exception e) {
			throw new PropertyAccessException(bean, name, e);
		}
		
		if (type == null) {
			throw new ArooaNoPropertyException(name, 
					bean.getClass(), 
					getClassName(bean).getBeanOverview(this).getProperties());
		}
		return type;
	}
	
	
	/**
	 * Get a property.
	 * 
	 * @param bean The bean.
	 * @param The property.
	 * @return The property value.
	 */
	public Object getProperty(Object bean, String property) 
	throws ArooaPropertyException {
		try {
			return propertyUtilsBean.getProperty(bean, property);
		} 
		catch (IndexOutOfBoundsException e) {
			// Return null if index access is out of bounds.
			return null;
		} 
		catch (BeanAccessLanguageException e) {
			throw new ArooaPropertyException(property, e);
		} 
		catch (IllegalAccessException e) {
			throw new ArooaPropertyException(property, e);
		} 
		catch (NoSuchMethodException e) {
			
			throw new PropertyExceptionBuilder(
					).forBean(bean
					).withOverview(getClassName(bean).getBeanOverview(this)
					).causedBy(e
					).failedReadingPropertyException(property);
		} 
		catch (InvocationTargetException e) {
			throw new ArooaPropertyException(property, e);
		}
	}
	
	/**
	 * Get a property of a required type.
	 * 
	 * @param bean The bean.
	 * @param The property.
	 * @return The property value.
	 */
	public <T> T getProperty(Object bean, 
			String property, Class<T> required) 
	throws ArooaPropertyException, ArooaConversionException {
		Object prop = getProperty(bean, property);
		
		if (converter == null) {
			return required.cast(prop);
		}
		else {
			return converter.convert(prop, required);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void validateSimplePropertyName(String name) 
	throws IllegalArgumentException {
		
		// we should really have a regular expression for this!
		if (name.indexOf(PropertyUtils.NESTED_DELIM) != -1) {
			throw new IllegalArgumentException("Nested property '" + name + "' is not supported in this context.");
		}
		if (name.indexOf(PropertyUtils.INDEXED_DELIM) != -1
				|| name.indexOf(PropertyUtils.INDEXED_DELIM2) != -1) {
			throw new IllegalArgumentException("Indexed property '" + name + "' is not supported in this context.");
		}
		if (name.indexOf(PropertyUtils.MAPPED_DELIM) != -1
				|| name.indexOf(PropertyUtils.MAPPED_DELIM2) != -1) {
			throw new IllegalArgumentException("Indexed property '" + name + "' is not supported in this context.");
		}
		
	}

	public ArooaClass getClassName(Object bean) {

		return ArooaClasses.classFor(bean);
		
	}
		
	@Override
	public BeanOverview getBeanOverview(Class<?> forClass)
			throws ArooaException {
		if (forClass == null) {
			throw new NullPointerException("No Arooa Class.");
		}
		
		BeanOverview overview = overviews.get(forClass);
		if (overview == null) {
				overview = new BeanUtilsBeanOverview(
						forClass, 
						propertyUtilsBean);
			overviews.put(forClass, overview);
		}	
		return overview;
	}
	
	public synchronized void clear() {
		overviews.clear();
		
	}
}
