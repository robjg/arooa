package org.oddjob.arooa.life;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

/**
 * Collect together everything to required to find {@link ElementMappings}.
 * 
 * @author rob
 *
 */
public class InstantiationContext {

	/** The type. */
	private final ArooaType arooaType;

	/** The class. */
	private final ArooaClass arooaClass;

	/** The class resolver. */
	private final ClassResolver classResolver;

	/** The converter. */
	private final ArooaConverter converter;
	
	/**
	 * Construct from context.
	 * 
	 * @param parentContext
	 */
	public InstantiationContext(ArooaContext parentContext) {
		this.arooaType = parentContext.getArooaType();
		
		RuntimeConfiguration runtime = parentContext.getRuntime();
		if (runtime == null) {
			this.arooaClass = null;
		}
		else {
			this.arooaClass = runtime.getClassIdentifier();
		}
		
		this.classResolver = parentContext.getSession(
				).getArooaDescriptor().getClassResolver();
		this.converter = parentContext.getSession().getTools(
				).getArooaConverter();
		
		validate();
	}
	
	/**
	 * 
	 * @param arooaType
	 * @param arooaClass
	 */
	public InstantiationContext(
			ArooaType arooaType, ArooaClass arooaClass) {
		this(arooaType, arooaClass, null, null);
	}
	
	/**
	 * Constructor with no converter.
	 * 
	 * @param arooaType
	 * @param arooaClass
	 * @param classResolver
	 */
	public InstantiationContext(
			ArooaType arooaType, ArooaClass arooaClass, 
			ClassResolver classResolver) {
		this(arooaType, arooaClass, classResolver, null);
	}
	
	/**
	 * Constructor with no {@link ClassResolver}.
	 * 
	 * @param arooaType
	 * @param arooaClass
	 * @param converter
	 */
	public InstantiationContext(
			ArooaType arooaType, ArooaClass arooaClass, 
			ArooaConverter converter) {
		this(arooaType, arooaClass, null, converter);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param arooaType Must not be null.
	 * @param arooaClass May be null.
	 * @param classResolver May be null.
	 * @param converter May be null.
	 */
	public InstantiationContext(
			ArooaType arooaType, ArooaClass arooaClass, 
			ClassResolver classResolver,
			ArooaConverter converter) {
		
		this.arooaType = arooaType;
		this.arooaClass = arooaClass;
		this.classResolver = classResolver;
		this.converter = converter;
		
		validate();
	}

	private void validate() {
		if (arooaType == null) {
			throw new NullPointerException("No ArooaType");
		}		
	}
	
	/**
	 * Getter for class.
	 * 
	 * @return The class. May be null.
	 */
	public ArooaClass getArooaClass() {
		return arooaClass;
	}
	
	/**
	 * Getter for type.
	 * 
	 * @return The type. Will not be null. 
	 */
	public ArooaType getArooaType() {
		return arooaType;
	}
	
	/**
	 * Get the {@link ClassResolver} if one is provided. May be null.
	 * 
	 * @return
	 */
	public ClassResolver getClassResolver() {
		return classResolver;
	}
	
	/**
	 * Get the {@link ArooaConverter} if one is provided. May be null.
	 * @return
	 */
	public ArooaConverter getArooaConverter() {
		return converter;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": arooaClass=" + arooaClass +
				", arooaType=" + arooaType + 
				", classResolver=" + classResolver + 
				", converter=" + converter;
	}
}
