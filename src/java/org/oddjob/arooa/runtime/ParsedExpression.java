package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;

/**
 * The result of parsing an expression with an {@link ExpressionParser}.
 * A ParsedExpression is an intermediate step to being evaluated.
 * 
 * @author rob
 */
public interface ParsedExpression {

	/**
	 * Evaluate the expression as an attribute.
	 * 
	 * @param session
	 * 
	 * @return An object result. May be null.
	 */
	public <T> T evaluateAsAttribute(ArooaSession session, Class<T> type)
	throws ArooaConversionException;
	
	/**
	 * Evaluate the expression as element text.
	 * 
	 * @param session
	 * 
	 * @return text result. May be null.
	 */
	public String evaluateAsText(ArooaSession session)
	throws ArooaConversionException;
	
	/**
	 * Is the expression constant as an attribute.
	 * 
	 * @return true/false.
	 */
	public boolean isConstantAttribute();
	
	/**
	 * Is the expression constant as element text.
	 * 
	 * @return true/false.
	 */
	public boolean isConstantText();
	
}
