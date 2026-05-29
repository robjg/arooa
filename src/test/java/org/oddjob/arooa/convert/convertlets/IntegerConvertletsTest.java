/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.hamcrest.MatcherAssert;
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

        ConversionLookup lookup = registry.get();

        ConversionPath<Number, Integer> path = lookup.findConversion(
                Number.class, Integer.class);

        Integer result = path.convert(new BigDecimal(42), null);

        assertEquals(Integer.valueOf(42), result);
    }

    @Test
    public void testStringToInteger() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new IntegerConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<String, Integer> path = lookup.findConversion(
                String.class, Integer.class);

        assertEquals("String-Integer", path.toString());

        Integer result = path.convert("2009090900", null);

        assertEquals(Integer.valueOf(2009090900), result);
    }

    @Test
    public void testNullIntegerToIntConversion() throws NoConversionAvailableException, ConversionFailedException {

        DefaultConverter converter = new DefaultConverter();

        int i = converter.convert(null, int.class);

        MatcherAssert.assertThat(i, is(0));

        Object o = converter.convert(null, int.class);

        MatcherAssert.assertThat(o, is(0));
    }

    @Test
    public void testIntegerToString() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<Integer, String> path = lookup.findConversion(
                Integer.class, String.class);

        assertEquals("Integer-String", path.toString());

        Object result = path.convert(2009090900, null);

        assertEquals("2009090900", result);
    }
}
