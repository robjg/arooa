package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ExpressionParser;

/**
 * Extends an existing tool set with an {@link ArooaConverter} based
 * on the provided {@link ArooaDescriptor}.
 * 
 * @author rob
 *
 */
public class ExtendedTools extends StandardTools {
	
	/** For type conversion. */
	private final ArooaConverter arooaConverter;

	/** For property access with type conversion. */
	private final PropertyAccessor propertyAccessor;
	
	/** For parsing attribute and text values. */
	private final ExpressionParser expressionParser;
	
	/**
	 * Uses the {@link ArooaDescriptor} to create the tools. 
	 * 
	 * @param descriptor
	 * @param loader
	 */
	public ExtendedTools(ArooaTools existing, 
			ArooaDescriptor descriptor) {

		if (descriptor == null) {
			throw new NullPointerException("No Descriptor.");
		}

		DefaultConversionRegistry registry = new DefaultConversionRegistry();

		ConversionProvider convertletProvider = 
			descriptor.getConvertletProvider();

		if (convertletProvider != null) {
			convertletProvider.registerWith(registry);
		}

		this.arooaConverter = new DefaultConverter(registry);
		this.propertyAccessor = existing.getPropertyAccessor();
		this.expressionParser = existing.getExpressionParser();
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
