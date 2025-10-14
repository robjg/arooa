package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.utils.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Evaluate an expression as a script using the Java Scripting API (defined by JSR 223).
 * <p/>
 * The script language can be set with the system property {@link #SCRIPT_LANGUAGE_PROPERTY}, it defaults
 * to JavaScript.
 * <p/>
 * <p>
 * This is the default Script Evaluator. An alternative can be set by providing an
 * {@link ScriptEvaluatorProvider} as a Service.
 * </p>
 * <p>
 * With JDK 11 you get a warning about Nashorn deprecation. This can suppressed with
 * {@code -Dnashorn.args=--no-deprecation-warning} or including the Nashorn jar explicitly in
 * the classpath. Building from source with JDK 9+ will do this.
 */
public class ScriptEvaluator implements Evaluator {

    private static final Logger logger = LoggerFactory.getLogger(ScriptEvaluator.class);

    public static final String SCRIPT_LANGUAGE_PROPERTY = "org.oddjob.script.language";

    public static final String DEFAULT_SCRIPT_LANGUAGE = "JavaScript";

    private final ScriptEngine engine;

    private ScriptEvaluator(ScriptEngine engine) {
        this.engine = Objects.requireNonNull(engine);
    }

    public static ScriptEvaluatorProvider asScriptEvaluatorProvider() {

        return classLoader -> {

            Try<ScriptEvaluator> evaluatorTry = getDefault(classLoader);

            return new Evaluator() {

                @Override
                public <T> T evaluate(String propertyExpression, ArooaSession session, Class<T> type) throws ArooaPropertyException, ArooaConversionException {
                    return evaluatorTry
                            .orElseThrow(t -> new IllegalArgumentException(
                                    "Failed to evaluate " + propertyExpression, t))
                            .evaluate(propertyExpression, session, type);
                }
            };
        };
    }

    @Override
    public <T> T evaluate(String propertyExpression, ArooaSession session, Class<T> type) throws ArooaPropertyException, ArooaConversionException {

        if (propertyExpression == null) {
            return null;
        }

        Object result;
        try {
            ScriptContext scriptContext = engine.getContext();
            scriptContext.setBindings(engine.createBindings(),
                    ScriptContext.ENGINE_SCOPE);
            scriptContext.setBindings(new SessionBindings(session.getBeanRegistry()),
                    ScriptContext.GLOBAL_SCOPE);
            result = engine.eval(propertyExpression,
                    scriptContext);
        } catch (ScriptException e) {
            throw new ArooaConversionException(e);
        }
        return session.getTools().getArooaConverter().convert(result, type);
    }

    public static ScriptEvaluator getDefault() {
        return getDefault(Thread.currentThread().getContextClassLoader()).orElseThrow();
    }

    public static Try<ScriptEvaluator> getDefault(ClassLoader classLoader) {
        String language = System.getProperty(SCRIPT_LANGUAGE_PROPERTY);
        if (language == null) {
            return getDefaultEngine(classLoader).map(ScriptEvaluator::new);
        } else {
            return getEngineByName(classLoader, language).map(ScriptEvaluator::new);
        }
    }

    public static Try<ScriptEngine> getDefaultEngine(ClassLoader classLoader) {

        return getEngineByName(classLoader, DEFAULT_SCRIPT_LANGUAGE);
    }

    public static Try<ScriptEngine> getEngineByName(ClassLoader classLoader, String language) {

        ScriptEngineManager manager = new ScriptEngineManager(
                classLoader);

        ScriptEngine engine = manager.getEngineByName(language);

        if (engine != null) {
            logger.debug("Using Script Engine {}", language);
            return Try.of(engine);
        }

        List<ScriptEngineFactory> availableEngineFactories = manager.getEngineFactories();

        if (availableEngineFactories.isEmpty()) {
            return Try.fail(new IllegalArgumentException("There are no Script Engines installed"));
        }

        String availableEngines = manager.getEngineFactories()
                .stream()
                .map(ScriptEngineFactory::getEngineName)
                .collect(Collectors.joining(", "));

        return Try.fail(new IllegalArgumentException(
                "There is no Script Engine [" + language + "], available engines are [" +
                        availableEngines + "]"));
    }
}
