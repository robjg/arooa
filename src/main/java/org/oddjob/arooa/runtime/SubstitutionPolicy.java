/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.runtime;

/**
 * Allow different behaviour during substitution. Intended to allow 
 * different handling of null values.
 */
public interface SubstitutionPolicy {

	/**
	 * Provide a modified {@link Evaluator}.
	 * 
	 * @param existingEvaluator An existing evaluator.
	 * 
	 * @return The modified evaluator.
	 */
	public Evaluator modify(Evaluator existingEvaluator);
}
