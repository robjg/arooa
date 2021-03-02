package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Optional;

/**
 * Evaluate an expression as a script using the Java Scripting API (defined by JSR 223).
 * <p/>
 * The script language can be set with the system property {@link #SCRIPT_LANGUAGE}, it defaults
 * to JavaScript.
 * <p/>
 * Mechanisms to use a plugin expression evaluator such as for EL may be considered in future
 * if this feature proves useful.
 */
public class ScriptEvaluator implements Evaluator {

    /**
     * Required for Java 11. Todo: Need to include our own scripting engine and not rely on JDK.
     */
    static {
        System.setProperty("nashorn.args", "--no-deprecation-warning");
    }

    public static final String SCRIPT_LANGUAGE = "org.oddjob.script.language";

    private final ScriptEngine engine;

    public ScriptEvaluator() {
        this(System.getProperty(SCRIPT_LANGUAGE));
    }

    public ScriptEvaluator(String language) {

        ScriptEngineManager manager = new ScriptEngineManager(
                Thread.currentThread().getContextClassLoader());

        this.engine = manager.getEngineByName(
                Optional.ofNullable(language).orElse("JavaScript"));
    }

    @Override
    public <T> T evaluate(String propertyExpression, ArooaSession session, Class<T> type) throws ArooaPropertyException, ArooaConversionException {

        if (propertyExpression == null) {
            return null;
        }

        Object result;
        try {
            ScriptContext scriptContext = engine.getContext();
            scriptContext.setBindings(new SessionBindings(session.getBeanRegistry()),
                    ScriptContext.GLOBAL_SCOPE);
            result = engine.eval(propertyExpression,
                    scriptContext);
        } catch (ScriptException e) {
            throw new ArooaConversionException(e);
        }
        return session.getTools().getArooaConverter().convert(result, type);
    }

}
