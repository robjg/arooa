package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.*;
import org.oddjob.arooa.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The standard implementation of {@link ArooaTools}.
 * 
 * @author rob
 *
 */
public class StandardTools implements ArooaTools {

	private static final Logger logger = LoggerFactory.getLogger(StandardTools.class);

	/** For type conversion. */
	private final ArooaConverter arooaConverter;

	/** For property access with type conversion. */
	private final PropertyAccessor propertyAccessor;
	
	/** For parsing attribute and text values. */
	private final ExpressionParser expressionParser;

	/** The evaluator to use for property resolution. */
	private final Evaluator evaluator;

	private volatile Evaluator scriptEvaluator;

	/**
	 * Default constructor.
	 */
	public StandardTools() {
		this.arooaConverter = new DefaultConverter();
		this.propertyAccessor = new BeanUtilsPropertyAccessor();
		this.expressionParser = new NestedExpressionParser();
		this.evaluator = new PropertyFirstEvaluator();
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
		if (scriptEvaluator == null) {
			synchronized (this) {
				if (scriptEvaluator == null) {
					ServiceLoader<ScriptEvaluatorProvider> loader = ServiceLoader.load(ScriptEvaluatorProvider.class);
					List<ScriptEvaluatorProvider> providers = StreamSupport.stream(loader.spliterator(), false)
							.collect(Collectors.toList());
					ScriptEvaluatorProvider provider;
					if (providers.isEmpty()) {
						provider = ScriptEvaluator.asScriptEvaluatorProvider();
					}
					else {
						provider = providers.get(0);
						if (logger.isDebugEnabled()) {
							if (providers.size() == 1) {
								logger.debug("Using Script Evaluator Provider [{}].", provider);
							} else {
								logger.debug("Using Script Evaluator Provider [{}] from [{}]", provider, providers);
							}
						}
					}
					scriptEvaluator = provider.provideScriptEvaluator(Thread.currentThread().getContextClassLoader());
				}
			}
		}
		return scriptEvaluator;
	}

	@Override
	public ServiceHelper getServiceHelper() {
		return context -> new CompositeServiceFinder(
				new ContextHierarchyServiceFinder(context),
				new DirectoryServiceFinder(
						context.getSession().getBeanRegistry()),
				new ComponentsServiceFinder(
						context.getSession().getComponentPool())
			);
	}
}
