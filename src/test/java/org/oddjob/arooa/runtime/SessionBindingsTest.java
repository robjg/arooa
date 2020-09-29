package org.oddjob.arooa.runtime;

import org.junit.Test;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.SimpleBeanRegistry;

import javax.script.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SessionBindingsTest {

    @Test
    public void testNashornAssumptions() throws ScriptException {

        ScriptEngineManager manager = new ScriptEngineManager();

        ScriptEngine engine = manager.getEngineByName("JavaScript");

        Bindings bindings = engine.createBindings();

        SimpleScriptContext context = new SimpleScriptContext();
        context.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        bindings.put("b", 2);
        bindings.put("c", 3);

        Object result = engine.eval("a = b + c", context);

        assertThat(result, is(5.0));
        assertThat(bindings.get("a"), is(5.0));
    }

    @Test
    public void testRegistryValuesAreUsed() throws ScriptException {

        ScriptEngineManager manager = new ScriptEngineManager();

        ScriptEngine engine = manager.getEngineByName("JavaScript");

        BeanRegistry registry = new SimpleBeanRegistry();
        registry.register("b", 2);
        registry.register("c", 3);

        Bindings bindings = new SessionBindings(registry);

        ScriptContext context = engine.getContext();
        context.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);

        Object result = engine.eval("a = b + c", context);

        Bindings engineBindings = context.getBindings(ScriptContext.ENGINE_SCOPE);

        assertThat(result, is(5.0));
        assertThat(engineBindings.get("a"), is(5.0));

    }

}