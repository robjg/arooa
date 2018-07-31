package org.oddjob.arooa.types;

import java.io.Serializable;

import javax.inject.Inject;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.utils.ClassUtils;

/**
 * @oddjob.description Returns a Class for the given name.
 * <p>
 * This class is not {@link Serializable} because it requires a ClassLoader
 * so it can not be used to set a property on a server from an Odjdob Client.
 * 
 * @oddjob.example
 * 
 * See {@link ConvertType} for an example.
 * 
 * @author rob
 *
 */
public class ClassType 
implements ArooaValue, ArooaSessionAware {
	
	public static final ArooaElement ELEMENT = new ArooaElement("class");
	
	public static class Conversions implements ConversionProvider {
		
		@SuppressWarnings("rawtypes")
		@Override
		public void registerWith(ConversionRegistry registry) {
			
			registry.register(ClassType.class, ArooaClass.class, 
					new Convertlet<ClassType, ArooaClass>() {
				@Override
				public ArooaClass convert(ClassType from)
				throws ConvertletException {
					try {
						return new SimpleArooaClass(from.toClass());
					} catch (ClassNotFoundException e) {
						throw new ConvertletException(e);
					}
				}
			});
			
			registry.register(ClassType.class, Class.class, 
					new Convertlet<ClassType, Class>() {
				@Override
				public Class<?> convert(ClassType from)
				throws ConvertletException {
					try {
						return from.toClass();
					} catch (ClassNotFoundException e) {
						throw new ConvertletException(e);
					}
				}
			});
		}
	}
	
	/**
	 * @oddjob.property 
	 * @oddjob.description The name of the class.
	 * @oddjob.required Yes.
	 */
	private String name;
	
	/**
	 * @oddjob.property 
	 * @oddjob.description The class loader to use to load the class.
	 * @oddjob.required No, defaults to Oddjob's class loader.
	 */
	private ClassLoader classLoader;
	
	/** Session from which to get the descriptor. */
	private ArooaSession session;
	
	@ArooaHidden
	@Override
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	public Class<?> toClass() throws ClassNotFoundException {
		if (name == null) {
			return null;
		}
		
		ClassLoader loader = this.classLoader;
		if (loader != null) {
			return ClassUtils.classFor(name, loader);
		} 
		
		Class<?> result = 
			session.getArooaDescriptor().getClassResolver().findClass(name);

		if (result == null) {
			throw new ClassNotFoundException(name);
		}
		return result;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Inject
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + name;
	}
}
