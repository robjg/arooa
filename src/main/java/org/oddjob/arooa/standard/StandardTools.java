package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.ComponentsServiceFinder;
import org.oddjob.arooa.registry.CompositeServiceFinder;
import org.oddjob.arooa.registry.ContextHierarchyServiceFinder;
import org.oddjob.arooa.registry.DirectoryServiceFinder;
import org.oddjob.arooa.registry.ServiceFinder;
import org.oddjob.arooa.registry.ServiceHelper;
import org.oddjob.arooa.runtime.*;

/**
 * The standard implementation of {@link ArooaTools}.
 * 
 * @author rob
 *
 */
public class StandardTools implements ArooaTools {

	/** For type conversion. */
	private final ArooaConverter arooaConverter;

	/** For property access with type conversion. */
	private final PropertyAccessor propertyAccessor;
	
	/** For parsing attribute and text values. */
	private final ExpressionParser expressionParser;

	/** The evaluator to use for property resolution. */
	private final Evaluator evaluator;

	private final Evaluator scriptEvaluator;

	/**
	 * Default constructor.
	 */
	public StandardTools() {
		this.arooaConverter = new DefaultConverter();
		this.propertyAccessor = new BeanUtilsPropertyAccessor();
		this.expressionParser = new NestedExpressionParser();
		this.evaluator = new PropertyFirstEvaluator();
		this.scriptEvaluator = new ScriptEvaluator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.ArooaTools#getArooaConverter()
	 */
	public ArooaConverter getArooaConverter() {	
		return arooaConverter;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.ArooaTools#getPropertyAccessor()
	 */
	public PropertyAccessor getPropertyAccessor() {
		return propertyAccessor;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.ArooaTools#getExpressionParser()
	 */
	public ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.ArooaTools#getEvaluator()
	 */
	@Override
	public Evaluator getEvaluator() {
		return evaluator;
	}

	/*
	 * @see org.oddjob.arooa.ArooaTools#getScriptEvaluator()
	 */
	@Override
	public Evaluator getScriptEvaluator() {
		return scriptEvaluator;
	}

	@Override
	public ServiceHelper getServiceHelper() {
		return new ServiceHelper() {
			
			@Override
			public ServiceFinder serviceFinderFor(ArooaContext context) {
				return new CompositeServiceFinder(
						new ContextHierarchyServiceFinder(context),
						new DirectoryServiceFinder(
								context.getSession().getBeanRegistry()),
						new ComponentsServiceFinder(
								context.getSession().getComponentPool())
					);
			}
		};
	}
}
