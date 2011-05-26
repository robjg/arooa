package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.parsing.ArooaElement;

/**
 * @oddjob.description Returns a Class for the given name.
 * 
 * @oddjob.example
 * 
 * See {@link ConvertType} for an example.
 * 
 * @author rob
 *
 */
public class ClassType implements ValueFactory<Class<?>>, ArooaSessionAware {

	public static final ArooaElement ELEMENT = new ArooaElement("class");
	
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
	private transient ArooaSession session;
	
	@ArooaHidden
	@Override
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	@Override
	public Class<?> toValue() throws ArooaConversionException {
		if (name == null) {
			return null;
		}
		
		ClassLoader loader = this.classLoader;
		if (loader != null) {
			try {
				return Class.forName(name, true, loader);
			}
			catch (ClassNotFoundException e) {
				throw new ArooaConversionException(e);
			}
		} 
		
		Class<?> result = 
			session.getArooaDescriptor().getClassResolver().findClass(name);

		if (result == null) {
			throw new ArooaConversionException(
					new ClassNotFoundException(name));
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

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
}
