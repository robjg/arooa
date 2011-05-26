package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ExpressionParser;

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
	
	/**
	 * Default constructor.
	 */
	public StandardTools() {
		this.arooaConverter = new DefaultConverter();
		this.propertyAccessor = new BeanUtilsPropertyAccessor();
		this.expressionParser = new StandardPropertyHelper();
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

}
