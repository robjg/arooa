package org.oddjob.arooa.runtime;

import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.SimpleBeanRegistry;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScriptEvaluatorTest {



    @Test
    public void simpleEvaluateAddingTwoVariables() throws ArooaConversionException {

        ArooaSession session = createSession();

        session.getBeanRegistry().register("a", 1);
        session.getBeanRegistry().register("b", 2);

        ScriptEvaluator test = new ScriptEvaluator();

        Integer result = test.evaluate("a+b", session, Integer.class);

        assertThat(result, is(3));
    }

    @Test
    public void testSettingSomething() throws ArooaConversionException {

        ArooaSession session = createSession();

        ScriptEvaluator test = new ScriptEvaluator();

        Integer result = test.evaluate("i=3", session, Integer.class);

        assertThat(result, is(3));

        assertThat(session.getBeanRegistry().lookup("i"), nullValue());
    }

    @Test
    public void testVoidMethod() throws ArooaConversionException {

        ArooaSession session = createSession();

        ScriptEvaluator test = new ScriptEvaluator();

        Integer result = test.evaluate("print('foo')", session, Integer.class);

        assertThat(result, nullValue());
    }

    @Test
    public void testEvaluateNull() throws ArooaConversionException {
        ArooaSession session = createSession();

        ScriptEvaluator test = new ScriptEvaluator();

        Integer result = test.evaluate(null, session, Integer.class);

        assertThat(result, nullValue());
    }

    @Test
    public void testEvaluateMissingVariable() {
        ArooaSession session = createSession();

        ScriptEvaluator test = new ScriptEvaluator();

        try {
            test.evaluate("idontexist", session, Integer.class);
            fail("Should fail");
        }
        catch (ArooaConversionException e) {
            assertThat(e.getMessage(), e.getMessage().contains("idontexist"), is(true));
        }
    }

    private ArooaSession createSession() {

        BeanRegistry beanRegistry = new SimpleBeanRegistry();

        ArooaTools tools = mock(ArooaTools.class);
        when(tools.getArooaConverter()).thenReturn(new DefaultConverter());

        ArooaSession session = mock(ArooaSession.class);
        when(session.getTools()).thenReturn(tools);
        when(session.getBeanRegistry()).thenReturn(beanRegistry);

        return session;
    }
}