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

	private final ArooaType arooaType;
	
	private final ArooaClass arooaClass;

	private final ClassResolver classResolver;
	
	private final ArooaConverter converter;
	
	/**
	 * Construct from context.
	 * 
	 * @param context
	 */
	public InstantiationContext(ArooaContext context) {
		this.arooaType = context.getArooaType();
		RuntimeConfiguration runtime = context.getRuntime();
		if (runtime == null) {
			this.arooaClass = null;
		}
		else {
			this.arooaClass = runtime.getClassIdentifier();
		}
		this.classResolver = context.getSession(
				).getArooaDescriptor().getClassResolver();
		this.converter = context.getSession().getTools(
				).getArooaConverter();
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
	
	
	public InstantiationContext(
			ArooaType arooaType, ArooaClass arooaClass, 
			ClassResolver classResolver,
			ArooaConverter converter) {
		this.arooaType = arooaType;
		this.arooaClass = arooaClass;
		this.classResolver = classResolver;
		this.converter = converter;
	}
	
	public ArooaClass getArooaClass() {
		return arooaClass;
	}
	
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
}
