package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.registry.BeanRegistry;

import javax.script.*;
import java.util.*;

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
            result = engine.eval(propertyExpression,
                    new SessionBindings(session.getBeanRegistry()));
        } catch (ScriptException e) {
            throw new ArooaConversionException(e);
        }
        return session.getTools().getArooaConverter().convert(result, type);
    }

    static class SessionBindings implements Bindings {

        private final Map<String, Object> local = new HashMap<>();

        private final BeanRegistry beanRegistry;

        SessionBindings(BeanRegistry beanRegistry) {
            this.beanRegistry = beanRegistry;
        }

        @Override
        public Object put(String name, Object value) {
            return local.put(name, value);
        }

        @Override
        public void putAll(Map<? extends String, ?> toMerge) {
            local.putAll(toMerge);
        }

        @Override
        public boolean containsKey(Object key) {
            return local.containsKey(key ) ||
                    beanRegistry.lookup(key.toString()) != null;
        }

        @Override
        public Object get(Object key) {
            if (local.containsKey(key)) {
                return local.get(key);
            }
            else {
                return beanRegistry.lookup(key.toString());
            }
        }

        @Override
        public Object remove(Object key) {
            throw new UnsupportedOperationException("Read Only");
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return local.containsValue(value);
        }

        @Override
        public void clear() {
            local.clear();
        }

        @Override
        public Set<String> keySet() {
            return local.keySet();
        }

        @Override
        public Collection<Object> values() {
            return local.values();
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return local.entrySet();
        }
    }
}
