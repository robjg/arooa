/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.convert.*;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;

public class IntegerConvertletsTest extends Assert {

    @Test
    public void testNumberToInteger() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new IntegerConvertlets().registerWith(registry);

        ConversionPath<Number, Integer> path = registry.findConversion(
                Number.class, Integer.class);

        Integer result = path.convert(new BigDecimal(42), null);

        assertEquals(new Integer(42), result);
    }

    @Test
    public void testStringToInteger() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new IntegerConvertlets().registerWith(registry);

        ConversionPath<String, Integer> path = registry.findConversion(
                String.class, Integer.class);

        assertEquals("String-Integer", path.toString());

        Integer result = path.convert("2009090900", null);

        assertEquals(new Integer(2009090900), result);
    }

    @Test
    public void testNullIntegerToIntConversion() throws NoConversionAvailableException, ConversionFailedException {

        DefaultConverter converter = new DefaultConverter();

        int i = converter.convert(null, int.class);

        assertThat(i, is(0));

        Object o = converter.convert(null, int.class);

        assertThat(o, is(0));
    }

    @Test
    public void testIntegerToString() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionPath<Integer, String> path = registry.findConversion(
                Integer.class, String.class);

        assertEquals("Integer-String", path.toString());

        Object result = path.convert(new Integer(2009090900), null);

        assertEquals("2009090900", result);
    }
}
