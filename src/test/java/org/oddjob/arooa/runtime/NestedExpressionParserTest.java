package org.oddjob.arooa.runtime;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaNoPropertyException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ValueFactory;

import static org.hamcrest.CoreMatchers.is;

public class NestedExpressionParserTest extends Assert {

    class TextExpressionChecker {

        StandardArooaSession session = new StandardArooaSession();

        public <T> void assertExpandsTo(String expression,
                                        T expected, Class<T> type)
                throws ArooaConversionException {

            NestedExpressionParser test = new NestedExpressionParser();

            ParsedExpression parsed = test.parse(expression);

            T result = parsed.evaluate(session, type);

            assertEquals(expression, expected, result);
        }

        public void assertExpandsTo(String expression, Object expected)
                throws ArooaConversionException {

            assertExpandsTo(expression, expected, Object.class);
        }
    }

    @Test
    public void testNewNestedParsing() throws ArooaConversionException {

        TextExpressionChecker checker = new TextExpressionChecker();
        checker.session.getBeanRegistry().register("fruit", "apple");
        checker.session.getBeanRegistry().register("snack", "fruit");
        checker.session.getBeanRegistry().register("foo", "itfru");
        checker.session.getBeanRegistry().register("fruitfruit", "applesandpears");
        checker.session.getBeanRegistry().register("applesandpears", "stairs");

        checker.assertExpandsTo("$${$${snack}}", "${${snack}}");

        checker.assertExpandsTo("${${snack}${snack}}", "applesandpears");
        checker.assertExpandsTo("${fru${foo}it}", "applesandpears");
        checker.assertExpandsTo("${${fru${foo}it}}", "stairs");
        checker.assertExpandsTo(" ${${fru${foo}it}} ", " stairs ");

        checker.assertExpandsTo("${${snack}}", "apple");
        checker.assertExpandsTo("${${doesntexist}}", null);
        checker.assertExpandsTo("a${${doesntexist}}b", "ab");

        checker.assertExpandsTo("$something", "$something");
        checker.assertExpandsTo("something$", "something$");

        checker.assertExpandsTo("${fruit}", "apple");

        checker.assertExpandsTo("${}", null);
        checker.assertExpandsTo("a${}b", "ab");

        checker.assertExpandsTo("My Fruit is ${fruit}", "My Fruit is apple");
        checker.assertExpandsTo("${fruit} is My Fruit", "apple is My Fruit");
        checker.assertExpandsTo("{${fruit}}", "{apple}");

        checker.assertExpandsTo("constant", "constant");
        checker.assertExpandsTo("apple$1", "apple$1");

        try {
            checker.assertExpandsTo("apple${", "apple${");
            fail("Should be syntax error.");
        } catch (ArooaException e) {
            // expected
        }
    }

    /**
     * run through the test cases of expansion
     *
     * @throws ArooaConversionException
     */
    @Test
    public void testPropertyExpansion() throws ArooaConversionException {

        TextExpressionChecker checker = new TextExpressionChecker();
        checker.session.getBeanRegistry().register("expanded", "EXPANDED");

        checker.assertExpandsTo("", "");
        checker.assertExpandsTo("$", "$");
        checker.assertExpandsTo("$$-", "$-");
        checker.assertExpandsTo("$$", "$");
        checker.assertExpandsTo("a${expanded}b", "aEXPANDEDb");
        checker.assertExpandsTo("${expanded}${expanded}", "EXPANDEDEXPANDED");
        checker.assertExpandsTo("$$$", "$$");
        checker.assertExpandsTo("$$$$-", "$$-");
        checker.assertExpandsTo("Class$$subclass", "Class$subclass");
        checker.assertExpandsTo("Class$subclass", "Class$subclass");
    }


    /**
     * new things we want
     *
     * @throws ArooaConversionException
     */
    @Test
    public void testDollarPassthru() throws ArooaConversionException {

        TextExpressionChecker checker = new TextExpressionChecker();

        checker.assertExpandsTo("$-", "$-");
        checker.assertExpandsTo("Class$subclass", "Class$subclass");
        checker.assertExpandsTo("$$$-", "$$-");
        checker.assertExpandsTo("$$$$$", "$$$");
        checker.assertExpandsTo("a${unassigned-property}", "a");
        checker.assertExpandsTo("a$b", "a$b");
        checker.assertExpandsTo("$}}", "$}}");
    }

    /**
     * Object replacement
     *
     * @throws ArooaConversionException
     */
    @Test
    public void testObjectReplacement() throws ArooaConversionException {
        TextExpressionChecker checker = new TextExpressionChecker();
        checker.session.getBeanRegistry().register("an-int", 2);

        checker.assertExpandsTo("${an-int}", 2);
        checker.assertExpandsTo("${unassigned}", null);
        checker.assertExpandsTo("${unassigned}", null);
        checker.assertExpandsTo("${unassigned.property}", null);

        checker.assertExpandsTo("", null, Integer.class);
        checker.assertExpandsTo("", 0, int.class);
    }

    public static class NestedProp {
        public String getBa() {
            return "Ba";
        }

        public NestedProp getBa2() {
            return new NestedProp();
        }
    }

    /**
     * Test nested expansion
     *
     * @throws ArooaConversionException
     */
    @Test
    public void testNestedExpansion() throws ArooaConversionException {

        TextExpressionChecker checker = new TextExpressionChecker();

        NestedProp foo = new NestedProp();
        checker.session.getBeanRegistry().register("foo", foo);

        checker.assertExpandsTo("${foo.ba}", "Ba");

        try {
            checker.assertExpandsTo("${foo.Ba}", null);
            fail("Ba doesn't exist");
        } catch (ArooaNoPropertyException expected) {
        }

        checker.assertExpandsTo("${foo.ba2.ba}", "Ba");
    }

    /**
     * Test isConstant method.
     */
    @Test
    public void testIsConstant() {

        NestedExpressionParser test = new NestedExpressionParser();

        ParsedExpression expression;

        expression = test.parse("abc");
        assertTrue(expression.isConstant());

        expression = test.parse("$abc");
        assertTrue(expression.isConstant());

        expression = test.parse("$$abc");
        assertTrue(expression.isConstant());

        expression = test.parse("$${abc}");
        assertTrue(expression.isConstant());

        expression = test.parse("${abc}");
        assertFalse(expression.isConstant());

        expression = test.parse("abc${abc}abc");
        assertFalse(expression.isConstant());
    }

    public static class MyArooaValue implements ValueFactory<String> {
        public String toValue() {
            return "apple";
        }
    }

    @Test
    public void testArooaValueExpansion() throws ArooaConversionException {
        TextExpressionChecker checker = new TextExpressionChecker();
        checker.session.getBeanRegistry().register(
                "fruit", new MyArooaValue());

        checker.assertExpandsTo("${fruit}s and pairs", "apples and pairs");
    }

    @Test
    public void testNullExpansionInString() throws ArooaConversionException {
        TextExpressionChecker checker = new TextExpressionChecker();

        checker.assertExpandsTo("apples and ${missing}", "apples and ");
    }

    @Test
    public void testNestedScriptParsing() throws ArooaConversionException {

        TextExpressionChecker checker = new TextExpressionChecker();
        checker.session.getBeanRegistry().register("one", "1");
        checker.session.getBeanRegistry().register("two", "2");
        checker.session.getBeanRegistry().register("fruit", "apple");

        checker.assertExpandsTo("##{##{snack}}", "#{#{snack}}");

        checker.assertExpandsTo("#{${one} + ${two}}", 3);
        checker.assertExpandsTo("#{#{one} + #{two}}", 3);

        checker.assertExpandsTo("#{}", null);
        checker.assertExpandsTo("a#{}b", "ab");

        checker.assertExpandsTo("My Fruit is #{fruit}", "My Fruit is apple");
        checker.assertExpandsTo("#{fruit} is My Fruit", "apple is My Fruit");
        checker.assertExpandsTo("{#{one+two}}", "{12}");

        try {
            checker.assertExpandsTo("#{foo}", "");
            fail("Should be syntax error.");
        } catch (ArooaConversionException e) {
            assertThat(e.getMessage(), e.getMessage().contains("foo"), is(true));
        }
    }
}
