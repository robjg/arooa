package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.parsing.SessionDelegate;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Create an {@link ArooaSession} with an {@link SubstitutionPolicy} 
 * applied to the {@link Evaluator}.
 *  
 * @author rob
 *
 */
public class SubstituationPolicySession extends SessionDelegate 
implements ArooaSession {

	private final Evaluator evaluator;

	private final ArooaTools toolsDelegate;
	
	/**
	 * Constructor.
	 * 
	 * @param delegate The existing session.
	 * @param substitutionPolicy The policy.
	 */
	public SubstituationPolicySession(ArooaSession delegate,
			SubstitutionPolicy substitutionPolicy) {
		super(delegate);
		toolsDelegate = delegate.getTools();
		evaluator = substitutionPolicy.modify(
				toolsDelegate.getEvaluator());
	}
	
	@Override
	public ArooaTools getTools() {
		return new ArooaTools() {
			
			@Override
			public PropertyAccessor getPropertyAccessor() {
				return toolsDelegate.getPropertyAccessor();
			}
			
			@Override
			public ExpressionParser getExpressionParser() {
				return toolsDelegate.getExpressionParser();
			}
			
			@Override
			public Evaluator getEvaluator() {
				return evaluator;
			}
			
			@Override
			public ArooaConverter getArooaConverter() {
				return toolsDelegate.getArooaConverter();
			}
		};
	}
}
