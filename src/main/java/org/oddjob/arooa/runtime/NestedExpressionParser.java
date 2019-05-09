package org.oddjob.arooa.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;

/**
 * Parses expressions that may contain references of the form 
 * ${property.ref}. Supports nested property evaluations of the
 * form ${${indirect.property.ref}}.
 * <p>
 * 
 * @author rob
 *
 */
public class NestedExpressionParser implements ExpressionParser {

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.runtime.ExpressionParser#parse(java.lang.String)
	 */
	@Override
	public ParsedExpression parse(String expression) {
		return recursiveParse(expression,
				new AtomicInteger(0), false);
	}
			
	/**
	 * Recursively called to evaluate expression.
	 * 
	 * @param value The expression.
	 * @param position Used as pass by reference.
	 * @param runtime True if inside a runtime evaluation.
	 * 
	 * @return A ParsedExpression.
	 */
	private ParsedExpression recursiveParse(String value,
											AtomicInteger position,
											boolean runtime) {
	
		CompositeParsedExpression expression = 
				new CompositeParsedExpression();
		
		int start = position.get();
		
		while (position.get() < value.length()) {
			
			// Current character
			char current = value.charAt(position.get());
			
			// Check if the end of a ${} expression.
			if (runtime && current == '}') {
				expression.addConstant(new ConstantExpression(
						value.substring(start, position.get())));
				position.incrementAndGet();
				return expression;
			}
						
			if (position.get() == value.length() -1) {
				position.incrementAndGet();
				continue;
			}				
				
			if (current == '$' || current == '#') {
				
				char following = value.charAt(position.get() + 1);
				
				if (following == '{') {
					
					// Save anything before this point as constant.
					if (position.get() > start) {
						expression.addConstant(new ConstantExpression(
								value.substring(start, position.get())));
					}

					position.addAndGet(2);
					
					ParsedExpression nested = recursiveParse(
							value, position, true);

					if ( current == '#' ) {
						expression.addRuntime(
								new ScriptExpression(nested));
					}
					else {
						expression.addRuntime(
								new RuntimeExpression(nested));
					}
					start = position.get();
					
					continue;
				}
				else if ((current == '$' && following == '$')
				|| current == '#' && following =='#' ) {
					// $$ becomes $ so split out what we have into
					// constant$
						expression.addConstant(new ConstantExpression(
								value.substring(start, 
										position.incrementAndGet())));

					// and move over the second $
					position.incrementAndGet();
					start = position.get();
					
					continue;
				}
			}

			// everything else just include
			position.incrementAndGet();
		}

		if (runtime) {
			throw new ArooaException("Syntax error in property: "
					 + value);
		}
		
		if (start < value.length()) {
			expression.addConstant(new ConstantExpression(
					value.substring(start)));
		}
		
		return expression;		
	}
	
	/**
	 * An expression that is just text.
	 * 
	 */
	class ConstantExpression implements ParsedExpression {
	
		private final String value;
		
		public ConstantExpression(String value) {
			if (value == null) {
				throw new NullPointerException("Value can't be null.");
			}
			this.value = value;
		}
		
		@Override
		public <T> T evaluate(ArooaSession session, Class<T> type)
				throws ArooaConversionException {
			return session.getTools().getArooaConverter().convert(
					value, type);
		}
		
		@Override
		public boolean isConstant() {
			return true;
		}
	}
	
	/**
	 * A runtime expression. Resolves a nested expression to a property
	 * identifier which it then evaluates.
	 * 
	 */
	class RuntimeExpression implements ParsedExpression {
		
		private final ParsedExpression expression;

		/**
		 * Constructor. 
		 *  
		 * @param expression The inner expression.
		 */
		public RuntimeExpression(ParsedExpression expression) {
			this.expression = expression;
		}
		
		@Override
		public <T> T evaluate(ArooaSession session, Class<T> type)
				throws ArooaConversionException {
			String propertyRef = expression.evaluate(session, String.class);
			Evaluator evaluator = session.getTools().getEvaluator();
			return evaluator.evaluate(propertyRef, session, type);
		}
		
		@Override
		public boolean isConstant() {
			return false;
		}
	}

	/**
	 *
	 */
	class ScriptExpression implements ParsedExpression {

		private final ParsedExpression expression;

		/**
		 * Constructor.
		 *
		 * @param expression The inner expression.
		 */
		public ScriptExpression(ParsedExpression expression) {
			this.expression = expression;
		}

		@Override
		public <T> T evaluate(ArooaSession session, Class<T> type)
				throws ArooaConversionException {
			String script = expression.evaluate(session, String.class);
			Evaluator evaluator = session.getTools().getScriptEvaluator();
			return evaluator.evaluate(script, session, type);
		}

		@Override
		public boolean isConstant() {
			return false;
		}
	}

	/**
	 * A collection of expression fragments.
	 * 
	 * @author rob
	 *
	 */
	class CompositeParsedExpression implements ParsedExpression {
		
		private final List<ParsedExpression> expressions =
				new ArrayList<>();
		
		private boolean constant = true;

		void addRuntime(ParsedExpression expression) {
			expressions.add(expression);
			constant = false;
		}
		
		void addConstant(ConstantExpression expression) {
			expressions.add(expression);
		}
		
		@Override
		public <T> T evaluate(ArooaSession session, Class<T> type)
				throws ArooaConversionException {
			if (expressions.size() == 1) {
				return expressions.get(0).evaluate(session, type);
			}
			
			StringBuilder builder = new StringBuilder();
			for (ParsedExpression expression : expressions) {
				String next = expression.evaluate(session, String.class);
				if (next != null) {
					builder.append(next);
				}
			}
			
	        ArooaConverter converter = 
	        		session.getTools().getArooaConverter();
		    
	        return converter.convert(builder.toString(), type);
		}
		
		@Override
		public boolean isConstant() {
			return constant;
		}		
	}
	
}
