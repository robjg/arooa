package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;

/**
 * The result of parsing an expression with an {@link ExpressionParser}.
 * A ParsedExpression is an intermediate step to being evaluated.
 * <p>
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
	public <T> T evaluate(ArooaSession session, Class<T> type)
	throws ArooaConversionException;
		
	/**
	 * Is the expression constant as an attribute.
	 * 
	 * @return true/false.
	 */
	public boolean isConstant();
	
}
