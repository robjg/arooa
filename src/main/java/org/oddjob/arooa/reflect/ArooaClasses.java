package org.oddjob.arooa.reflect;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.NoConversionAvailableException;

public class ArooaClasses {

	private static final DefaultConversionRegistry registry =
		new DefaultConversionRegistry();
	
	
	@SuppressWarnings({ "rawtypes" })
	public static <T> void register(Class<T> ofType,
			final ArooaClassFactory<T> factory) {
	
		Convertlet<T, ArooaClassFactory> convertlet = new
			Convertlet<T, ArooaClassFactory>() {
				public ArooaClassFactory convert(T from) 
				throws ConvertletException {
					return factory;
				}
		};
		
		registry.register(ofType, ArooaClassFactory.class, convertlet);
	}
	
	@SuppressWarnings("unchecked")
	public static ArooaClass classFor(Object object) {
		if (object == null) {
			throw new NullPointerException("No oject.");
		}
		
		ArooaClassFactory<Object> factory = null;
		
		try { 
			
			ArooaConverter converter = new DefaultConverter(registry);
			

			factory = converter.convert(object, ArooaClassFactory.class);
		}
		catch (NoConversionAvailableException e) {
			throw new IllegalArgumentException(
					"No ArooaClassFactory for bean of class " + 
					object.getClass() + 
					". If this is a test ensure " +
					BeanUtilsPropertyAccessor.class.getName() + 
					" class is loaded.");
		}
		catch (ConversionFailedException e) {
			throw new RuntimeException("Failed finding class for " + 
					object, e);
		}
		
		return factory.classFor(object);
	}
}
