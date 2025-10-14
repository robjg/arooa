package org.oddjob.arooa.runtime;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;

import javax.script.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Used to understand bindings in Nashorn. The class {@link NashornScriptEngine}
 * is where most of the logic for this is.
 *
 */
class ScriptBindingsAssumptionsTest {

    /**
     * When we use our own bindings, Nashorn creates its own global
     * bindings and inserts them in ours as 'nashorn.global'
     */
    @Test
    void simpleBindings() throws ScriptException {

        ScriptEngine engine = ScriptEvaluator
                .getDefaultEngine(getClass().getClassLoader())
                .orElseThrow();

        Bindings engineBindings = new SimpleBindings();
        Bindings globalBindings = new SimpleBindings();

        ScriptContext context = engine.getContext();
        context.setBindings(engineBindings, ScriptContext.ENGINE_SCOPE);
        context.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE);

        assertThat(engine.getBindings(ScriptContext.ENGINE_SCOPE),
                sameInstance(engineBindings));
        assertThat(engine.getBindings(ScriptContext.GLOBAL_SCOPE),
                sameInstance(globalBindings));

        engineBindings.put("b", 2);
        globalBindings.put("c", 3);

        Number result = (Number) engine.eval("a = b + c", context);

        assertThat(result.intValue(), is(5));

        assertThat(engineBindings.get("a"), nullValue());
        assertThat(globalBindings.get("a"), nullValue());

        assertThat(engine.getBindings(ScriptContext.ENGINE_SCOPE),
                sameInstance(engineBindings));
        assertThat(engine.getBindings(ScriptContext.GLOBAL_SCOPE),
                sameInstance(globalBindings));

        assertThat(((Bindings) engineBindings.get("nashorn.global"))
                .get("a"), is(5.0));
        assertThat(globalBindings.size(), is(1));
    }

    @Test
    void engineCreateBindings() throws ScriptException {

        ScriptEngine engine = ScriptEvaluator
                .getDefaultEngine(getClass().getClassLoader())
                .orElseThrow();

        Bindings engineBindings = engine.createBindings();
        Bindings globalBindings = engine.createBindings();

        assertThat(engineBindings, not(sameInstance(globalBindings)));
        assertThat(engineBindings, not(is(globalBindings)));

        ScriptContext context = engine.getContext();
        context.setBindings(engineBindings, ScriptContext.ENGINE_SCOPE);
        context.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE);

        engineBindings.put("b", 2);
        globalBindings.put("c", 3);

        Object result = engine.eval("a = b + c", context);

        assertThat(result, is(5.0));

        assertThat(engineBindings.get("a"), is(5.0));
        assertThat(globalBindings.keySet(), Matchers.containsInAnyOrder("c"));
        // Debug into this, and we see that the script is evaluated again and
        // the return result is used.
        assertThat(globalBindings.get("a"), is(5.0));

        assertThat(engine.getBindings(ScriptContext.ENGINE_SCOPE),
                sameInstance(engineBindings));
        assertThat(engine.getBindings(ScriptContext.GLOBAL_SCOPE),
                sameInstance(globalBindings));
    }

    /**
     * Var declared 'a' also appears in Global scope. Not sure that it should:
     * <a href="https://stackoverflow.com/questions/2485423/is-using-var-to-declare-variables-optional">Var</a>
     */
    @Test
    void engineBindingsVar() throws ScriptException {

        ScriptEngine engine = ScriptEvaluator
                .getDefaultEngine(getClass().getClassLoader())
                .orElseThrow();

        Bindings engineBindings = engine.createBindings();
        Bindings globalBindings = engine.createBindings();

        assertThat(engineBindings, not(sameInstance(globalBindings)));
        assertThat(engineBindings, not(is(globalBindings)));

        engineBindings.put("b", 2);
        globalBindings.put("c", 3);

        assertThat(engineBindings.get("c"), nullValue());
        assertThat(globalBindings.get("b"), nullValue());

        ScriptContext context = engine.getContext();
        context.setBindings(engineBindings, ScriptContext.ENGINE_SCOPE);
        context.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE);

        assertThat(engine.getBindings(ScriptContext.ENGINE_SCOPE)
                .get("c"), is(3));
        assertThat(engine.getBindings(ScriptContext.GLOBAL_SCOPE)
                .get("b"), is(2));

        Object result = engine.eval("var a = b + c", context);

        assertThat(result, nullValue());

        assertThat(engineBindings.get("a"), is(5.0));
        assertThat(globalBindings.get("a"), is(5.0));
    }

    /**
     * Default context has Engine Bindings that are Nashorn's, and Global
     * that are Simple.
     */
    @Test
    void engineContextBindings() throws ScriptException {

        ScriptEngine engine = ScriptEvaluator
                .getDefaultEngine(getClass().getClassLoader())
                .orElseThrow();

        ScriptContext context = engine.getContext();

        Bindings engineBindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
        assertThat(engineBindings, sameInstance(engine.getBindings(ScriptContext.ENGINE_SCOPE)));
        Bindings globalBindings = context.getBindings(ScriptContext.GLOBAL_SCOPE);
        assertThat(globalBindings, sameInstance(engine.getBindings(ScriptContext.GLOBAL_SCOPE)));

        assertThat(engineBindings, not(sameInstance(globalBindings)));
        assertThat(engineBindings, not(is(globalBindings)));

        engineBindings.put("b", 2);
        globalBindings.put("c", 3);

        Object result = engine.eval("a = b + c", context);

        assertThat(result, is(5.0));

        assertThat(engineBindings.get("a"), is(5.0));
        assertThat(globalBindings.get("a"), nullValue());

        assertThat(engine.getBindings(ScriptContext.ENGINE_SCOPE)
                .get("a"), is(5.0));
        assertThat(engine.getBindings(ScriptContext.GLOBAL_SCOPE)
                .get("a"), nullValue());
    }

}
