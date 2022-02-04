package org.oddjob.arooa.runtime;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.SimpleBeanRegistry;

import javax.script.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SessionBindingsTest {

    @BeforeClass
    public static void classSetUp() throws ClassNotFoundException {
        // Accessing a static constant is not enough to load the ScriptEvaluator class
        // and turn off Nashorn deprecation warnings.
        Class.forName(ScriptEvaluator.class.getName());
    }

    @Test
    public void testEngineAssumptions() throws ScriptException {

        ScriptEngine engine = ScriptEvaluator
                .getDefaultEngine(getClass().getClassLoader())
                .orElseThrow();

        Bindings bindings = engine.createBindings();

        SimpleScriptContext context = new SimpleScriptContext();
        context.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        bindings.put("b", 2);
        bindings.put("c", 3);

        Number result = (Number) engine.eval("a = b + c", context);

        assertThat(result.intValue(), is(5));

        Number a = (Number) bindings.get("a");

        assertThat(a.intValue(), is(5));
    }

    @Test
    public void testRegistryValuesAreUsed() throws ScriptException {

        ScriptEngine engine = ScriptEvaluator.getDefaultEngine(getClass().getClassLoader())
                .orElseThrow();

        BeanRegistry registry = new SimpleBeanRegistry();
        registry.register("b", 2);
        registry.register("c", 3);

        Bindings bindings = new SessionBindings(registry);

        ScriptContext context = engine.getContext();
        context.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);

        Number result = (Number) engine.eval("a = b + c", context);

        assertThat(result.intValue(), is(5));

        Bindings engineBindings = context.getBindings(ScriptContext.ENGINE_SCOPE);

        Number a = (Number) engineBindings.get("a");

        assertThat(a.intValue(), is(5));
    }
}