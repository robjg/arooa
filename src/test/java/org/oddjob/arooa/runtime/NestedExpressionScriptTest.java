package org.oddjob.arooa.runtime;

import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.reflect.ArooaPropertyException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NestedExpressionScriptTest {

    @SuppressWarnings("unchecked")
    static class ScriptCapture {

        List<String> evaluated = new ArrayList<>();

        String evaluate(String expression) throws ArooaConversionException {

            NestedExpressionParser test = new NestedExpressionParser();

            ParsedExpression parsedExpression =
                    test.parse(expression);

            Evaluator scriptEval = new Evaluator() {
                @Override
                public <T> T evaluate(String propertyExpression, ArooaSession session, Class<T> type) throws ArooaPropertyException {
                    evaluated.add(propertyExpression);

                    return (T) ("Evaluated:" + Integer.valueOf(evaluated.size()).toString());
                }
            };

            Evaluator propertyEval = new Evaluator() {
                @Override
                public <T> T evaluate(String propertyExpression, ArooaSession session, Class<T> type) throws ArooaPropertyException {
                    evaluated.add(propertyExpression);

                    return (T) propertyExpression.toUpperCase();
                }
            };

            ArooaConverter arooaConverter = mock(ArooaConverter.class);
            doAnswer(invocation -> invocation.getArguments()[0])
                    .when(arooaConverter)
                    .convert(any(), any());

            ArooaTools arooaTools = mock(ArooaTools.class);
            when(arooaTools.getScriptEvaluator())
                    .thenReturn(scriptEval);
            when(arooaTools.getEvaluator())
                    .thenReturn(propertyEval);
            when(arooaTools.getArooaConverter())
                    .thenReturn(arooaConverter);

            ArooaSession arooaSession = mock(ArooaSession.class);
            when(arooaSession.getTools()).thenReturn(arooaTools);

            return parsedExpression.evaluate(arooaSession, String.class);
        }
    }

    @Test
    public void whenScriptFunctionThenTextPreserved() throws ArooaConversionException {

        ScriptCapture scriptCapture = new ScriptCapture();

        assertThat(scriptCapture.evaluate("#{function foo(x) { x + 2 }}"),
                is("Evaluated:1"));
        assertThat(scriptCapture.evaluated.get(0),
                is("function foo(x) { x + 2 }"));
    }

    @Test
    public void whenPropertyInScriptBothEvaluated() throws ArooaConversionException {

        ScriptCapture scriptCapture = new ScriptCapture();

        assertThat(scriptCapture.evaluate("#{x => { x + ${some.num} }}"),
                is("Evaluated:2"));
        assertThat(scriptCapture.evaluated.get(0),
                is("some.num"));
        assertThat(scriptCapture.evaluated.get(1),
                is("x => { x + SOME.NUM }"));
    }

    @Test
    public void whenTwoScripts() throws ArooaConversionException {

        ScriptCapture scriptCapture = new ScriptCapture();

        assertThat(scriptCapture.evaluate("#{{foo}} and #{{bar}}"),
                is("Evaluated:1 and Evaluated:2"));
        assertThat(scriptCapture.evaluated.get(0),
                is("{foo}"));
        assertThat(scriptCapture.evaluated.get(1),
                is("{bar}"));
    }

    @Test
    public void whenLotsOfBrackets() throws ArooaConversionException {

        ScriptCapture scriptCapture = new ScriptCapture();

        assertThat(scriptCapture.evaluate("#{x => { x {{Y}{Z}} }}"),
                is("Evaluated:1"));
        assertThat(scriptCapture.evaluated.get(0),
                is("x => { x {{Y}{Z}} }"));
    }
}
