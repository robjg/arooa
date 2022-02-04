package org.oddjob.arooa.runtime;

/**
 * Allow modules to provide a Script {@link Evaluator} to be used for the evaluation of <code>#{}</code> expressions.
 *
 * @see org.oddjob.arooa.standard.StandardTools
 */
public interface ScriptEvaluatorProvider {

    Evaluator provideScriptEvaluator(ClassLoader classLoader);
}
