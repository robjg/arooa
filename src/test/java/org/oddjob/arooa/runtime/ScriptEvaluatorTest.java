package org.oddjob.arooa.runtime;

import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.SimpleBeanRegistry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScriptEvaluatorTest {


    @Test
    public void simpleEvaluateAddingTwoVariables() throws ArooaConversionException {

        ArooaSession session = createSession();

        session.getBeanRegistry().register("a", 1);
        session.getBeanRegistry().register("b", 2);

        ScriptEvaluator test = ScriptEvaluator.getDefault();

        Integer result = test.evaluate("a+b", session, Integer.class);

        assertThat(result, is(3));
    }

    @Test
    public void simpleEvaluatingMethods() throws ArooaConversionException {

        ArooaSession session = createSession();

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        session.getBeanRegistry().register("list", list);

        ScriptEvaluator test = ScriptEvaluator.getDefault();

        @SuppressWarnings("unchecked")
        Collection<Integer> result = test.evaluate(
                "list.stream().limit(3).collect(java.util.stream.Collectors.toList())",
                session,
                Collection.class);

        assertThat(result, is(Arrays.asList(1, 2, 3)));
    }

    public static class SomeBean {

        private String foo;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }

    @Test
    public void simpleEvaluatingPropertiesOfBeans() throws ArooaConversionException {

        ArooaSession session = createSession();

        SomeBean bean = new SomeBean();
        bean.setFoo("Foo");

        session.getBeanRegistry().register("bean", bean);

        ScriptEvaluator test = ScriptEvaluator.getDefault();

        String result = test.evaluate(
                "bean.foo",
                session,
                String.class);

        assertThat(result, is("Foo"));
    }

    @Test
    public void testSettingSomethingDoesWriteBack() throws ArooaConversionException {

        ArooaSession session = createSession();

        ScriptEvaluator test = ScriptEvaluator.getDefault();

        Integer result = test.evaluate("i=3", session, Integer.class);

        assertThat(result, is(3));

        assertThat(session.getBeanRegistry().lookup("i"), nullValue());
    }

    @Test
    public void testVoidMethodEvaluateToNull() throws ArooaConversionException {

        ArooaSession session = createSession();

        ScriptEvaluator test = ScriptEvaluator.getDefault();

        Integer result = test.evaluate("print('foo')", session, Integer.class);

        assertThat(result, nullValue());
    }

    @Test
    public void testEvaluateNullIsNull() throws ArooaConversionException {
        ArooaSession session = createSession();

        ScriptEvaluator test = ScriptEvaluator.getDefault();

        Integer result = test.evaluate(null, session, Integer.class);

        assertThat(result, nullValue());
    }

    @Test
    public void testEvaluateMissingVariableThrowException() {
        ArooaSession session = createSession();

        ScriptEvaluator test = ScriptEvaluator.getDefault();

        try {
            test.evaluate("idontexist", session, Integer.class);
            fail("Should fail");
        } catch (ArooaConversionException e) {
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