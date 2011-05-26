package org.oddjob.arooa.runtime;

/**
 * Something that is able to parse an expression which is either an
 * attribute in a configuration or element text.
 * 
 * @author rob
 *
 */
public interface ExpressionParser {

	/**
	 * Parse the expression.
	 * 
	 * @param expression The expression.
	 * 
	 * @return A parsed expression ready for evaluation.
	 */
	public ParsedExpression parse(String expression);
}
