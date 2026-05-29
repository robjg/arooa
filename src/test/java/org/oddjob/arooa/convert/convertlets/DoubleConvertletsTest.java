/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.convert.*;

public class DoubleConvertletsTest extends Assert {

    @Test
    public void testNumberToDouble() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DoubleConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<Number, Double> path = lookup.findConversion(
                Number.class, Double.class);

        Double result = path.convert(42.24f, null);

        assertEquals(42.24, result, 0.001);
    }

    @Test
    public void testStringToDouble() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DoubleConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<String, Double> path = lookup.findConversion(
                String.class, Double.class);

        Double result = path.convert("42.24", null);

        assertEquals(42.24, result, 0.001);
    }

    @Test
    public void testDoubleToString() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<Double, String> path = lookup.findConversion(
                Double.class, String.class);

        assertEquals("Double-String", path.toString());

        String result = path.convert(42.24, null);

        MatcherAssert.assertThat(result, Matchers.is("42.24"));
    }
}
